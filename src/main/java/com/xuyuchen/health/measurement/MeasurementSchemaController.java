package com.xuyuchen.health.measurement;

import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/internal/projects/{projectId}/schema")
public class MeasurementSchemaController {
    private final MeasurementSchemaService service;
    public MeasurementSchemaController(MeasurementSchemaService service) { this.service = service; }
    @PutMapping("/{version}")
    public MeasurementSchemaService.Schema put(@PathVariable String projectId, @PathVariable int version, @RequestBody Map<String, String> fields) { return service.configure(projectId, version, fields); }
    @GetMapping("/{version}")
    public MeasurementSchemaService.Schema get(@PathVariable String projectId, @PathVariable int version) { return service.get(projectId, version); }
}
