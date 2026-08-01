package com.xuyuchen.health.device;

import java.util.List;
import java.util.Optional;

public interface DeviceRepository {
    Device save(Device device);
    Optional<Device> find(String projectId, String deviceId);
    List<Device> findByProject(String projectId);
    List<Device> findAll();
}
