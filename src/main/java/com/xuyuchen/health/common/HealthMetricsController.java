package com.xuyuchen.health.common;

import com.xuyuchen.health.measurement.MeasurementMetrics;
import com.xuyuchen.health.measurement.MessageDeduplicationService;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/internal/health")
public class HealthMetricsController {
    private final MeasurementMetrics metrics;
    private final MessageDeduplicationService dedup;
    public HealthMetricsController(MeasurementMetrics metrics, MessageDeduplicationService dedup) { this.metrics = metrics; this.dedup = dedup; }
    @GetMapping
    public Map<String, Object> health() { return Map.of("status", "UP", "checkedAt", Instant.now(), "metrics", metrics.snapshot(), "dedupKeys", dedup.size()); }
}
