package com.xuyuchen.health.measurement;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Repository
@Primary
public class InMemoryLatestMeasurementStore implements LatestMeasurementStore {
    private final ConcurrentMap<String, Measurement> latest = new ConcurrentHashMap<>();
    private String key(String projectId, String deviceId, String channelId) { return projectId + ":" + deviceId + ":" + channelId; }
    @Override public boolean putIfNewer(Measurement measurement) {
        String key = key(measurement.projectId(), measurement.deviceId(), measurement.channelId());
        final boolean[] accepted = {false};
        latest.compute(key, (k, old) -> {
            if (old == null || old.eventTime().isBefore(measurement.eventTime()) || (old.eventTime().equals(measurement.eventTime()) && old.sequence() < measurement.sequence())) {
                accepted[0] = true; return measurement;
            }
            return old;
        });
        return accepted[0];
    }
    @Override public Optional<Measurement> get(String projectId, String deviceId, String channelId) { return Optional.ofNullable(latest.get(key(projectId, deviceId, channelId))); }
    @Override public List<Measurement> latestByDevice(String projectId, String deviceId) {
        return latest.values().stream().filter(m -> m.projectId().equals(projectId) && m.deviceId().equals(deviceId)).toList();
    }
}
