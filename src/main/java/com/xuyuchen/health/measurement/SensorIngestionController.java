package com.xuyuchen.health.measurement;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/projects/{projectId}/ingestion")
public class SensorIngestionController {
    private final SensorIngestionService service;
    public SensorIngestionController(SensorIngestionService service) { this.service = service; }
    public record RawRequest(@NotBlank String deviceId, @NotBlank String messageId, Instant receivedAt, @Valid Map<String, Object> payload) {}
    @PostMapping("/sensor")
    public MeasurementBatchService.BatchResult sensor(@PathVariable String projectId, @Valid @RequestBody RawRequest req) {
        return service.ingest(new RawSensorMessage(projectId, req.deviceId(), req.messageId(), req.receivedAt() == null ? Instant.now() : req.receivedAt(), req.payload()));
    }
}
