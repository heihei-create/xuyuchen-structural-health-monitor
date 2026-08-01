package com.xuyuchen.health.measurement;

import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestController
@RequestMapping("/internal/projects/{projectId}/export")
public class MeasurementExportController {
    private final MeasurementExportService service;
    public MeasurementExportController(MeasurementExportService service) { this.service = service; }
    @GetMapping("/{deviceId}/{channelId}")
    public MeasurementExportService.ExportedBatch export(@PathVariable String projectId, @PathVariable String deviceId, @PathVariable String channelId, @RequestParam Instant from, @RequestParam Instant to, @RequestParam(defaultValue = "10000") int limit) {
        return service.export(projectId, deviceId, channelId, from, to, limit);
    }
}
