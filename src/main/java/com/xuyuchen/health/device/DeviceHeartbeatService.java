package com.xuyuchen.health.device;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;

@Service
public class DeviceHeartbeatService {
    private final DeviceRepository devices;
    private final Duration offlineAfter = Duration.ofMinutes(2);
    public DeviceHeartbeatService(DeviceRepository devices) { this.devices = devices; }
    public void record(String projectId, String deviceId, Instant at) {
        Device device = devices.find(projectId, deviceId).orElseThrow(() -> new IllegalArgumentException("device not found"));
        device.seen(at); devices.save(device);
    }
    @Scheduled(fixedDelayString = "${health.device-reaper-delay-ms:30000}")
    public void markOffline() {
        Instant threshold = Instant.now().minus(offlineAfter);
        devices.findAll().stream().filter(d -> d.getStatus() == DeviceStatus.ONLINE && d.getLastSeen().isBefore(threshold)).forEach(device -> { device.offline(); devices.save(device); });
    }
}
