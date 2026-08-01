package com.xuyuchen.health.measurement;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/events")
public class MeasurementEventController {
    private final InMemoryMeasurementEventPublisher publisher;
    public MeasurementEventController(InMemoryMeasurementEventPublisher publisher) { this.publisher = publisher; }
    @GetMapping("/measurements/recent")
    public List<Measurement> recent(@RequestParam(defaultValue = "100") int limit) { return publisher.recent(Math.min(Math.max(1, limit), 500)); }
}
