package com.xuyuchen.health.measurement;

import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/internal/projects/{projectId}/quality")
public class MeasurementQualityController {
    private final MeasurementMetrics metrics;
    private final MessageDeduplicationService dedup;
    private final DataQualityService quality;
    public MeasurementQualityController(MeasurementMetrics metrics, MessageDeduplicationService dedup, DataQualityService quality) {
        this.metrics = metrics; this.dedup = dedup; this.quality = quality;
    }
    @GetMapping
    public Map<String, Object> report(@PathVariable String projectId) { return Map.of("projectId", projectId, "generatedAt", Instant.now(), "metrics", metrics.snapshot(), "dedupKeys", dedup.size()); }
}
