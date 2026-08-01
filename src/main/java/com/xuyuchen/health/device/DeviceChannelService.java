package com.xuyuchen.health.device;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DeviceChannelService {
    private final DeviceChannelRepository channels;
    private final DeviceService devices;
    public DeviceChannelService(DeviceChannelRepository channels, DeviceService devices) { this.channels = channels; this.devices = devices; }
    public DeviceChannel register(String projectId, String deviceId, String channelId, String metric, String unit, double min, double max, int interval) {
        devices.get(projectId, deviceId);
        if (max <= min || interval < 1) throw new IllegalArgumentException("invalid channel range or interval");
        if (channels.find(projectId, deviceId, channelId).isPresent()) throw new IllegalStateException("channel already exists");
        DeviceChannel channel = new DeviceChannel(projectId, deviceId, channelId, metric, unit, min, max, interval);
        devices.get(projectId, deviceId).addChannel(channelId); return channels.save(channel);
    }
    public DeviceChannel get(String p, String d, String c) { return channels.find(p, d, c).orElseThrow(() -> new IllegalArgumentException("channel not found")); }
    public List<DeviceChannel> list(String p, String d) { return channels.findByDevice(p, d); }
}
