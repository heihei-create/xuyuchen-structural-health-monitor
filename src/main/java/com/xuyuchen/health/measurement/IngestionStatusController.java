package com.xuyuchen.health.measurement;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/internal/projects/{projectId}/ingestion-status")
public class IngestionStatusController {
    private final IngestionStatusService service;
    public IngestionStatusController(IngestionStatusService service) { this.service = service; }
    @GetMapping
    public IngestionStatusService.Status status(@PathVariable String projectId) { return service.status(projectId); }
}
