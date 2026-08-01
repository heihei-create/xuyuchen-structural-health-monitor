package com.xuyuchen.health.measurement;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import com.xuyuchen.health.device.DeviceTokenService;

@RestController
@RequestMapping("/api/v1/projects/{projectId}/measurements/batch")
public class MeasurementBatchController {
    private final MeasurementBatchService service;
    private final DeviceTokenService tokens;
    public MeasurementBatchController(MeasurementBatchService service, DeviceTokenService tokens) { this.service = service; this.tokens = tokens; }
    @PostMapping
    public MeasurementBatchService.BatchResult ingest(@PathVariable String projectId, @RequestHeader("X-Device-Token") String token, @Valid @RequestBody List<MeasurementDtos.IngestRequest> requests) {
        if (requests == null || requests.isEmpty() || requests.stream().anyMatch(req -> !tokens.valid(token, projectId, req.deviceId()))) throw new IllegalArgumentException("invalid device token");
        return service.ingest(requests.stream().map(req -> req.toMeasurement(projectId)).toList());
    }
}
