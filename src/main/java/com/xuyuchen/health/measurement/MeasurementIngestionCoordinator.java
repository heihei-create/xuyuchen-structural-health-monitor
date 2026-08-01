package com.xuyuchen.health.measurement;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class MeasurementIngestionCoordinator {
    private final SensorMessageDecoder decoder;
    private final MeasurementBatchService batch;
    private final DataQualityService quality;
    private final MeasurementMetrics metrics;
    public MeasurementIngestionCoordinator(SensorMessageDecoder decoder, MeasurementBatchService batch, DataQualityService quality, MeasurementMetrics metrics) {
        this.decoder = decoder; this.batch = batch; this.quality = quality; this.metrics = metrics;
    }
    public MeasurementBatchService.BatchResult process(RawSensorMessage raw) {
        List<Measurement> decoded = decoder.decode(raw);
        List<Measurement> valid = decoded.stream().filter(value -> {
            DataQualityResult result = quality.validate(value, raw.receivedAt() == null ? Instant.now() : raw.receivedAt());
            if (!result.accepted()) metrics.invalid();
            return result.accepted();
        }).toList();
        return batch.ingest(valid);
    }
}
