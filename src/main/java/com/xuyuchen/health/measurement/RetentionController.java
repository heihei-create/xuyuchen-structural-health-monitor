package com.xuyuchen.health.measurement;

import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.time.Duration;

@RestController
@RequestMapping("/internal/projects/{projectId}/retention")
public class RetentionController {
    private final RetentionPolicyService service;
    public RetentionController(RetentionPolicyService service) { this.service = service; }
    public record RetentionRequest(long rawSeconds, long aggregateSeconds, int downsampleSeconds) {}
    @PutMapping
    public RetentionPolicy put(@PathVariable String projectId, @Valid @RequestBody RetentionRequest req) {
        return service.configure(projectId, new RetentionPolicy(Duration.ofSeconds(req.rawSeconds()), Duration.ofSeconds(req.aggregateSeconds()), req.downsampleSeconds()));
    }
    @GetMapping
    public RetentionPolicy get(@PathVariable String projectId) { return service.get(projectId); }
}
