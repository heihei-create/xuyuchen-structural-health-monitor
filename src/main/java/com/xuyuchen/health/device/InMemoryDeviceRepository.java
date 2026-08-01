package com.xuyuchen.health.device;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Repository
public class InMemoryDeviceRepository implements DeviceRepository {
    private final ConcurrentMap<String, Device> records = new ConcurrentHashMap<>();
    private String key(String projectId, String deviceId) { return projectId + ":" + deviceId; }
    @Override public Device save(Device device) { records.put(key(device.getProjectId(), device.getDeviceId()), device); return device; }
    @Override public Optional<Device> find(String projectId, String deviceId) { return Optional.ofNullable(records.get(key(projectId, deviceId))); }
    @Override public List<Device> findByProject(String projectId) { return records.values().stream().filter(d -> d.getProjectId().equals(projectId)).toList(); }
    @Override public List<Device> findAll() { return records.values().stream().toList(); }
}
