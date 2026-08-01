package com.xuyuchen.health.device;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Repository
public class InMemoryDeviceChannelRepository implements DeviceChannelRepository {
    private final ConcurrentMap<String, DeviceChannel> records = new ConcurrentHashMap<>();
    private String key(String p, String d, String c) { return p + ":" + d + ":" + c; }
    @Override public DeviceChannel save(DeviceChannel value) { records.put(key(value.projectId(), value.deviceId(), value.channelId()), value); return value; }
    @Override public Optional<DeviceChannel> find(String p, String d, String c) { return Optional.ofNullable(records.get(key(p, d, c))); }
    @Override public List<DeviceChannel> findByDevice(String p, String d) { return records.values().stream().filter(v -> v.projectId().equals(p) && v.deviceId().equals(d)).toList(); }
}
