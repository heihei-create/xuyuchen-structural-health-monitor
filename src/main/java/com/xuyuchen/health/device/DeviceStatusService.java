package com.xuyuchen.health.device;

import org.springframework.stereotype.Service;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Service
public class DeviceStatusService {
    private final DeviceRepository devices;
    public DeviceStatusService(DeviceRepository devices) { this.devices = devices; }
    public Map<DeviceStatus, Long> summary(String projectId) {
        EnumMap<DeviceStatus, Long> result = new EnumMap<>(DeviceStatus.class);
        for (DeviceStatus status : DeviceStatus.values()) result.put(status, 0L);
        devices.findByProject(projectId).forEach(device -> result.compute(device.getStatus(), (key, value) -> value + 1));
        return result;
    }
    public List<Device> offline(String projectId) { return devices.findByProject(projectId).stream().filter(d -> d.getStatus() == DeviceStatus.OFFLINE).toList(); }
}
