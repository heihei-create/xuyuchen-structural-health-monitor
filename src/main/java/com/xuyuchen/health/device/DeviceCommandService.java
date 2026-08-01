package com.xuyuchen.health.device;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class DeviceCommandService {
    private final DeviceService devices;
    private final CopyOnWriteArrayList<DeviceCommand> commands = new CopyOnWriteArrayList<>();
    public DeviceCommandService(DeviceService devices) { this.devices = devices; }
    public DeviceCommand create(String projectId, String deviceId, String command, String payload) {
        devices.get(projectId, deviceId);
        DeviceCommand value = new DeviceCommand(UUID.randomUUID(), projectId, deviceId, command, payload, Instant.now(), null, null);
        commands.add(value); return value;
    }
    public DeviceCommand ack(String projectId, String deviceId, UUID id) {
        DeviceCommand command = find(id);
        if (!command.projectId().equals(projectId) || !command.deviceId().equals(deviceId)) throw new IllegalArgumentException("command not found");
        return replace(id, command.ack());
    }
    public DeviceCommand fail(UUID id, String reason) { return replace(id, find(id).fail(reason)); }
    public List<DeviceCommand> list(String projectId, String deviceId) { return commands.stream().filter(c -> c.projectId().equals(projectId) && c.deviceId().equals(deviceId)).toList(); }
    private DeviceCommand find(UUID id) { return commands.stream().filter(c -> c.id().equals(id)).findFirst().orElseThrow(() -> new IllegalArgumentException("command not found")); }
    private DeviceCommand replace(UUID id, DeviceCommand value) { commands.removeIf(c -> c.id().equals(id)); commands.add(value); return value; }
}
