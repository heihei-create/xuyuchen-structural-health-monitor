package com.xuyuchen.health.measurement;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Comparator;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class WindowAggregator {
    private final Map<WindowKey, WindowAggregate> windows = new ConcurrentHashMap<>();
    public WindowAggregate add(Measurement measurement, int seconds) {
        if (seconds < 1 || seconds > 3600) throw new IllegalArgumentException("window size out of range");
        long start = (measurement.eventTime().getEpochSecond() / seconds) * seconds;
        WindowKey key = new WindowKey(measurement.projectId(), measurement.deviceId(), measurement.channelId(), start, seconds);
        WindowAggregate aggregate = windows.computeIfAbsent(key, WindowAggregate::new);
        aggregate.add(measurement); return aggregate;
    }
    public List<WindowAggregate> list(String projectId, String deviceId, String channelId, int seconds) {
        return windows.values().stream().filter(w -> w.key().projectId().equals(projectId) && w.key().deviceId().equals(deviceId) && w.key().channelId().equals(channelId) && w.key().seconds() == seconds)
                .sorted(Comparator.comparingLong(w -> w.key().windowStartEpochSecond())).toList();
    }
    public int removeBefore(Instant cutoff) {
        int[] removed = {0}; windows.keySet().removeIf(key -> { boolean match = key.windowStartEpochSecond() < cutoff.getEpochSecond(); if (match) removed[0]++; return match; }); return removed[0];
    }
}
