package com.xuyuchen.health.device;

import jakarta.validation.constraints.Min;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/projects/{projectId}/devices/{deviceId}/tokens")
public class DeviceTokenController {
    private final DeviceTokenService service;
    public DeviceTokenController(DeviceTokenService service) { this.service = service; }
    @PostMapping
    public Map<String, Object> issue(@PathVariable String projectId, @PathVariable String deviceId, @RequestParam(defaultValue = "86400") @Min(60) long seconds) {
        return Map.of("token", service.issue(projectId, deviceId, seconds), "expiresIn", seconds);
    }
    @DeleteMapping("/{token}")
    public void revoke(@PathVariable String projectId, @PathVariable String deviceId, @PathVariable String token) { service.revoke(projectId, deviceId, token); }
}
