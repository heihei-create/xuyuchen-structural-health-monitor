package com.xuyuchen.health.measurement;

import jakarta.validation.constraints.Min;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/internal/ingestion")
public class IngestionControlController {
    private final ProjectRateLimiter limiter;
    private final IngestionQueue queue;
    public IngestionControlController(ProjectRateLimiter limiter, IngestionQueue queue) { this.limiter = limiter; this.queue = queue; }
    public record LimitRequest(@Min(1) long capacity, double refillPerSecond) {}
    public record QueueRequest(@Min(1) int queueLimit, boolean dropOldest, boolean sampleLatest) {}
    @PutMapping("/{projectId}/limit")
    public Map<String, Object> limit(@PathVariable String projectId, @RequestBody LimitRequest req) { limiter.configure(projectId, req.capacity(), req.refillPerSecond()); return Map.of("projectId", projectId, "available", limiter.available(projectId)); }
    @PutMapping("/{projectId}/backpressure")
    public Map<String, Object> backpressure(@PathVariable String projectId, @RequestBody QueueRequest req) { queue.configure(projectId, new BackpressurePolicy(req.queueLimit(), req.dropOldest(), req.sampleLatest())); return Map.of("projectId", projectId, "queueSize", queue.size(projectId)); }
    @GetMapping("/{projectId}")
    public Map<String, Object> status(@PathVariable String projectId) { return Map.of("projectId", projectId, "availableTokens", limiter.available(projectId), "queueSize", queue.size(projectId)); }
}
