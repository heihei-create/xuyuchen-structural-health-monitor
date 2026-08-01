package com.xuyuchen.health.measurement;

import java.time.Instant;
import java.util.List;

public interface MeasurementHistoryStore {
    void append(Measurement measurement);
    List<Measurement> query(String projectId, String deviceId, String channelId, Instant from, Instant to, int limit);
}
