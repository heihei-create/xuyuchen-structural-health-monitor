package com.xuyuchen.health.measurement;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/projects/{projectId}/measurements/batch")
public class MeasurementBatchController {
    private final MeasurementBatchService service;
    public MeasurementBatchController(MeasurementBatchService service) { this.service = service; }
    @PostMapping
    public MeasurementBatchService.BatchResult ingest(@PathVariable String projectId, @Valid @RequestBody List<MeasurementDtos.IngestRequest> requests) {
        return service.ingest(requests.stream().map(req -> req.toMeasurement(projectId)).toList());
    }
}
