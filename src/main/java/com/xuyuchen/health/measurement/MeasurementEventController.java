package com.xuyuchen.health.measurement;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.ObjectProvider;

import java.util.List;

@RestController
@RequestMapping("/api/v1/events")
public class MeasurementEventController {
    private final ObjectProvider<InMemoryMeasurementEventPublisher> publisher;
    public MeasurementEventController(ObjectProvider<InMemoryMeasurementEventPublisher> publisher) { this.publisher = publisher; }
    @GetMapping("/measurements/recent")
    public List<Measurement> recent(@RequestParam(defaultValue = "100") int limit) { InMemoryMeasurementEventPublisher value = publisher.getIfAvailable(); return value == null ? List.of() : value.recent(Math.min(Math.max(1, limit), 500)); }
}
