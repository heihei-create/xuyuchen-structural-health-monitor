package com.xuyuchen.health.device;

import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/projects/{projectId}/devices/{deviceId}/commands")
public class DeviceCommandController {
    private final DeviceCommandService service;
    public DeviceCommandController(DeviceCommandService service) { this.service = service; }
    public record CommandRequest(@NotBlank String command, String payload) {}
    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public DeviceCommand create(@PathVariable String projectId, @PathVariable String deviceId, @jakarta.validation.Valid @RequestBody CommandRequest req) { return service.create(projectId, deviceId, req.command(), req.payload()); }
    @PostMapping("/{commandId}/ack")
    public DeviceCommand ack(@PathVariable String projectId, @PathVariable String deviceId, @PathVariable UUID commandId) { return service.ack(projectId, deviceId, commandId); }
    @GetMapping
    public List<DeviceCommand> list(@PathVariable String projectId, @PathVariable String deviceId) { return service.list(projectId, deviceId); }
}
