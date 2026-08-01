package com.xuyuchen.health.device;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.xuyuchen.health.device.ProjectDtos.*;

@RestController
@RequestMapping("/api/v1/projects/{projectId}/devices")
public class DeviceController {
    private final DeviceService service;
    public DeviceController(DeviceService service) { this.service = service; }
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Device provision(@PathVariable String projectId, @Valid @RequestBody CreateDeviceRequest req) {
        return service.provision(projectId, req.deviceId(), req.name(), req.model(), req.firmware(), req.channels());
    }
    @GetMapping
    public List<Device> list(@PathVariable String projectId) { return service.list(projectId); }
    @GetMapping("/{deviceId}")
    public Device get(@PathVariable String projectId, @PathVariable String deviceId) { return service.get(projectId, deviceId); }
    @PostMapping("/{deviceId}/disable")
    public Device disable(@PathVariable String projectId, @PathVariable String deviceId) { return service.disable(projectId, deviceId); }
}
