package com.xuyuchen.health.measurement;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/internal/replay")
public class ReplayController {
    private final ReplayService service;
    public ReplayController(ReplayService service) { this.service = service; }
    @PostMapping
    public MeasurementBatchService.BatchResult replay(@RequestParam(defaultValue = "1") double speed, @Valid @RequestBody List<Measurement> records) { return service.replay(records, speed); }
}
