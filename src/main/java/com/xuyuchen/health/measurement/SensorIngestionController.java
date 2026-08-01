package com.xuyuchen.health.measurement;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Map;
import com.xuyuchen.health.device.DeviceTokenService;

@RestController
@RequestMapping("/api/v1/projects/{projectId}/ingestion")
public class SensorIngestionController {
    private final SensorIngestionService service;
    private final DeviceTokenService tokens;
    public SensorIngestionController(SensorIngestionService service, DeviceTokenService tokens) { this.service = service; this.tokens = tokens; }
    public record RawRequest(@NotBlank String deviceId, @NotBlank String messageId, Instant receivedAt, @Valid Map<String, Object> payload) {}
    @PostMapping("/sensor")
    public MeasurementBatchService.BatchResult sensor(@PathVariable String projectId, @RequestHeader("X-Device-Token") String token, @Valid @RequestBody RawRequest req) {
        if (!tokens.valid(token, projectId, req.deviceId())) throw new IllegalArgumentException("invalid device token");
        return service.ingest(new RawSensorMessage(projectId, req.deviceId(), req.messageId(), req.receivedAt() == null ? Instant.now() : req.receivedAt(), req.payload()));
    }
}
