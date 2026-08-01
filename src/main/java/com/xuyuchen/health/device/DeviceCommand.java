package com.xuyuchen.health.device;

import java.time.Instant;
import java.util.UUID;

public record DeviceCommand(UUID id, String projectId, String deviceId, String command, String payload, Instant createdAt, Instant acknowledgedAt, String error) {
    public DeviceCommand ack() { return new DeviceCommand(id, projectId, deviceId, command, payload, createdAt, Instant.now(), null); }
    public DeviceCommand fail(String reason) { return new DeviceCommand(id, projectId, deviceId, command, payload, createdAt, null, reason); }
}
