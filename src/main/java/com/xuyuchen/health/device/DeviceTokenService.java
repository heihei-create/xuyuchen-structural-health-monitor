package com.xuyuchen.health.device;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class DeviceTokenService {
    private record Token(String projectId, String deviceId, Instant expiresAt) {}
    private final Map<String, Token> tokens = new ConcurrentHashMap<>();
    private final DeviceRepository devices;
    public DeviceTokenService(DeviceRepository devices) { this.devices = devices; }
    public String issue(String projectId, String deviceId, long seconds) {
        devicesCheck(projectId, deviceId);
        String value = UUID.randomUUID().toString();
        tokens.put(value, new Token(projectId, deviceId, Instant.now().plusSeconds(Math.max(60, seconds))));
        return value;
    }
    public boolean valid(String token, String projectId, String deviceId) {
        Token value = tokens.get(token);
        return value != null && value.projectId().equals(projectId) && value.deviceId().equals(deviceId) && value.expiresAt().isAfter(Instant.now());
    }
    public void revoke(String projectId, String deviceId, String token) {
        if (!valid(token, projectId, deviceId)) throw new IllegalArgumentException("device token not found");
        tokens.remove(token);
    }
    private void devicesCheck(String projectId, String deviceId) {
        if (projectId == null || projectId.isBlank() || deviceId == null || deviceId.isBlank()) throw new IllegalArgumentException("project and device required");
        if (devices.find(projectId, deviceId).isEmpty()) throw new IllegalArgumentException("device not found");
    }
}
