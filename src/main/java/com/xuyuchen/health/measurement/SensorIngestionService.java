package com.xuyuchen.health.measurement;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SensorIngestionService {
    private final SensorMessageDecoder decoder;
    private final MeasurementBatchService batch;
    private final ProjectRateLimiter limiter;
    public SensorIngestionService(SensorMessageDecoder decoder, MeasurementBatchService batch, ProjectRateLimiter limiter) { this.decoder = decoder; this.batch = batch; this.limiter = limiter; }
    public MeasurementBatchService.BatchResult ingest(RawSensorMessage raw) {
        List<Measurement> measurements = decoder.decode(raw);
        if (!limiter.allow(raw.projectId(), measurements.size())) throw new IllegalStateException("project ingestion rate exceeded");
        return batch.ingest(measurements);
    }
}
