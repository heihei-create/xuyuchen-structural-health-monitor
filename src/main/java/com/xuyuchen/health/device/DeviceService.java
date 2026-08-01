package com.xuyuchen.health.device;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class DeviceService {
    private final DeviceRepository devices;
    private final ProjectAccessService access;
    public DeviceService(DeviceRepository devices, ProjectAccessService access) { this.devices = devices; this.access = access; }
    public Device provision(String projectId, String deviceId, String name, String model, String firmware, List<String> channels) {
        access.require(projectId);
        if (devices.find(projectId, deviceId).isPresent()) throw new IllegalStateException("device already exists");
        Device device = new Device(projectId, deviceId, name, model, firmware);
        if (channels != null) channels.forEach(device::addChannel);
        return devices.save(device);
    }
    public Device get(String projectId, String deviceId) { return devices.find(projectId, deviceId).orElseThrow(() -> new IllegalArgumentException("device not found")); }
    public List<Device> list(String projectId) { access.require(projectId); return devices.findByProject(projectId); }
    public Device online(String projectId, String deviceId) { Device d = get(projectId, deviceId); d.online(); return devices.save(d); }
    public Device seen(String projectId, String deviceId, Instant eventTime) { Device d = get(projectId, deviceId); d.seen(eventTime); return devices.save(d); }
    public Device disable(String projectId, String deviceId) { Device d = get(projectId, deviceId); d.disable(); return devices.save(d); }
    public void markOffline(String projectId, String deviceId) { Device d = get(projectId, deviceId); d.offline(); devices.save(d); }
}
