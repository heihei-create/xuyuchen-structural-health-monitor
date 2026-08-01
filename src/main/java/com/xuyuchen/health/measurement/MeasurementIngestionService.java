package com.xuyuchen.health.measurement;

import com.xuyuchen.health.device.Device;
import com.xuyuchen.health.device.DeviceService;
import com.xuyuchen.health.alert.AlertEvent;
import com.xuyuchen.health.alert.AlertService;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class MeasurementIngestionService {
    private final DeviceService devices;
    private final MessageDeduplicationService dedup;
    private final LatestMeasurementStore latest;
    private final MeasurementStorageService storage;
    private final MeasurementEventPublisher publisher;
    private final AlertService alerts;
    private final MeasurementMetrics metrics;
    private final DataQualityService quality;
    public MeasurementIngestionService(DeviceService devices, MessageDeduplicationService dedup, LatestMeasurementStore latest, MeasurementStorageService storage, MeasurementEventPublisher publisher, AlertService alerts, MeasurementMetrics metrics, DataQualityService quality) {
        this.devices = devices; this.dedup = dedup; this.latest = latest; this.storage = storage; this.publisher = publisher; this.alerts = alerts; this.metrics = metrics; this.quality = quality;
    }
    public MeasurementDtos.IngestResponse ingest(Measurement measurement) {
        DataQualityResult qualityResult = quality.validate(measurement, Instant.now());
        if (!qualityResult.accepted()) { metrics.invalid(); throw new IllegalArgumentException(qualityResult.reason()); }
        Device device = devices.get(measurement.projectId(), measurement.deviceId());
        if (device.getStatus() == com.xuyuchen.health.device.DeviceStatus.DISABLED) throw new IllegalArgumentException("device disabled");
        if (!device.getChannels().isEmpty() && !device.getChannels().contains(measurement.channelId())) throw new IllegalArgumentException("channel not registered");
        MessageDeduplicationService.Reservation reservation = dedup.reserve(measurement.projectId(), measurement.messageId());
        if (reservation == null) { metrics.duplicate(); return response(measurement, false, true, false); }
        try {
            boolean accepted = reservation.retryable() || latest.putIfNewer(measurement);
            boolean outOfOrder = !accepted;
            if (!reservation.retryable()) dedup.markDerived(reservation, accepted);
            if (outOfOrder) metrics.outOfOrder();
            storage.persist(measurement, accepted);
            if (!outOfOrder) {
                devices.seen(measurement.projectId(), measurement.deviceId(), measurement.eventTime());
                publisher.publish(measurement);
                AlertEvent alert = alerts.evaluate(measurement.projectId(), measurement.deviceId(), measurement.channelId(), measurement.value(), measurement.eventTime());
                if (alert != null && alert.status() != com.xuyuchen.health.alert.AlertStatus.NORMAL) metrics.alert();
            }
            dedup.commit(reservation);
            metrics.accepted();
            return response(measurement, true, false, outOfOrder);
        } catch (RuntimeException failure) {
            dedup.release(reservation);
            throw failure;
        }
    }
    private MeasurementDtos.IngestResponse response(Measurement m, boolean accepted, boolean duplicate, boolean outOfOrder) {
        String latestValue = latest.get(m.projectId(), m.deviceId(), m.channelId()).map(v -> String.valueOf(v.value())).orElse(null);
        return new MeasurementDtos.IngestResponse(m.projectId(), m.deviceId(), m.channelId(), accepted, duplicate, outOfOrder, latestValue);
    }
}
