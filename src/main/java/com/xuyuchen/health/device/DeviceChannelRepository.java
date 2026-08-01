package com.xuyuchen.health.device;

import java.util.List;
import java.util.Optional;

public interface DeviceChannelRepository {
    DeviceChannel save(DeviceChannel channel);
    Optional<DeviceChannel> find(String projectId, String deviceId, String channelId);
    List<DeviceChannel> findByDevice(String projectId, String deviceId);
}
