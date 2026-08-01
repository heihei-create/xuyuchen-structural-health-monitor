package com.xuyuchen.health.measurement;

import com.xuyuchen.health.device.DeviceService;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;

@Service
public class ProjectTelemetrySummaryService {
    private final DeviceService devices;
    private final MeasurementMetrics metrics;
    private final IngestionStatusService ingestion;
    public ProjectTelemetrySummaryService(DeviceService devices, MeasurementMetrics metrics, IngestionStatusService ingestion) {
        this.devices = devices; this.metrics = metrics; this.ingestion = ingestion;
    }
    public Summary summary(String projectId) {
        return new Summary(projectId, Instant.now(), devices.list(projectId).size(), devices.list(projectId).stream().filter(d -> d.getStatus() == com.xuyuchen.health.device.DeviceStatus.ONLINE).count(), metrics.snapshot(), ingestion.status(projectId).queueSize(), Map.of("source", "realtime"));
    }
    public record Summary(String projectId, Instant generatedAt, int devices, long onlineDevices, MeasurementMetrics.Snapshot metrics, int queueSize, Map<String, String> labels) {}
}
