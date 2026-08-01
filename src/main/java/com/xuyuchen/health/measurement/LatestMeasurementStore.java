package com.xuyuchen.health.measurement;

import java.util.List;
import java.util.Optional;

public interface LatestMeasurementStore {
    boolean putIfNewer(Measurement measurement);
    Optional<Measurement> get(String projectId, String deviceId, String channelId);
    List<Measurement> latestByDevice(String projectId, String deviceId);
}
