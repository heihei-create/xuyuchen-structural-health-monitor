package com.xuyuchen.health.measurement;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;

@Service
public class IngestionStatusService {
    private final MeasurementMetrics metrics;
    private final ProjectRateLimiter limiter;
    private final IngestionQueue queue;
    public IngestionStatusService(MeasurementMetrics metrics, ProjectRateLimiter limiter, IngestionQueue queue) {
        this.metrics = metrics; this.limiter = limiter; this.queue = queue;
    }
    public Status status(String projectId) {
        return new Status(projectId, Instant.now(), metrics.snapshot(), limiter.available(projectId), queue.size(projectId));
    }
    public record Status(String projectId, Instant checkedAt, MeasurementMetrics.Snapshot metrics, double availableTokens, int queueSize) {}
}
