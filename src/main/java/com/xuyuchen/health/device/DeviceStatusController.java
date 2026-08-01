package com.xuyuchen.health.device;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/projects/{projectId}/device-status")
public class DeviceStatusController {
    private final DeviceStatusService service;
    public DeviceStatusController(DeviceStatusService service) { this.service = service; }
    @GetMapping("/summary")
    public Map<DeviceStatus, Long> summary(@PathVariable String projectId) { return service.summary(projectId); }
    @GetMapping("/offline")
    public List<Device> offline(@PathVariable String projectId) { return service.offline(projectId); }
}
