package com.xuyuchen.health.device;

import java.time.Instant;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class Device {
    private final String projectId;
    private final String deviceId;
    private final String name;
    private final String model;
    private final Set<String> channels = ConcurrentHashMap.newKeySet();
    private volatile DeviceStatus status;
    private volatile Instant lastSeen;
    private volatile String firmware;
    public Device(String projectId, String deviceId, String name, String model, String firmware) {
        this.projectId = projectId; this.deviceId = deviceId; this.name = name; this.model = model; this.firmware = firmware;
        this.status = DeviceStatus.PROVISIONED; this.lastSeen = Instant.now();
    }
    public String getProjectId() { return projectId; }
    public String getDeviceId() { return deviceId; }
    public String getName() { return name; }
    public String getModel() { return model; }
    public Set<String> getChannels() { return Set.copyOf(channels); }
    public DeviceStatus getStatus() { return status; }
    public Instant getLastSeen() { return lastSeen; }
    public String getFirmware() { return firmware; }
    public void addChannel(String channelId) { channels.add(channelId); }
    public void online() { status = DeviceStatus.ONLINE; lastSeen = Instant.now(); }
    public void seen(Instant time) { lastSeen = time; status = DeviceStatus.ONLINE; }
    public void offline() { if (status != DeviceStatus.DISABLED) status = DeviceStatus.OFFLINE; }
    public void disable() { status = DeviceStatus.DISABLED; }
    public void updateFirmware(String value) { firmware = value; }
}
