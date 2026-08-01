package com.xuyuchen.health.measurement;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/internal/projects/{projectId}/partitions")
public class PartitionController {
    private final MeasurementPartitionService service;
    public PartitionController(MeasurementPartitionService service) { this.service = service; }
    @GetMapping
    public List<MeasurementPartition> list(@PathVariable String projectId) { return service.list(projectId); }
}
