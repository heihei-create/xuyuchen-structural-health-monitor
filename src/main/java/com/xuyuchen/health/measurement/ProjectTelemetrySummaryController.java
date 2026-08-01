package com.xuyuchen.health.measurement;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/internal/projects/{projectId}/telemetry-summary")
public class ProjectTelemetrySummaryController {
    private final ProjectTelemetrySummaryService service;
    public ProjectTelemetrySummaryController(ProjectTelemetrySummaryService service) { this.service = service; }
    @GetMapping
    public ProjectTelemetrySummaryService.Summary get(@PathVariable String projectId) { return service.summary(projectId); }
}
