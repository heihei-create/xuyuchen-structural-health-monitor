package com.xuyuchen.health.measurement;

import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Repository
public class InMemoryMeasurementHistoryStore implements MeasurementHistoryStore {
    private final CopyOnWriteArrayList<Measurement> records = new CopyOnWriteArrayList<>();
    @Override public void append(Measurement measurement) { records.add(measurement); }
    @Override public List<Measurement> query(String projectId, String deviceId, String channelId, Instant from, Instant to, int limit) {
        return records.stream().filter(m -> m.projectId().equals(projectId) && m.deviceId().equals(deviceId) && m.channelId().equals(channelId))
                .filter(m -> from == null || !m.eventTime().isBefore(from)).filter(m -> to == null || !m.eventTime().isAfter(to))
                .sorted(Comparator.comparing(Measurement::eventTime).reversed()).limit(Math.min(Math.max(1, limit), 10000)).toList();
    }
}
