package com.xuyuchen.health.measurement;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class MeasurementQueryService {
    private final LatestMeasurementStore latest;
    private final MeasurementHistoryStore history;
    public MeasurementQueryService(LatestMeasurementStore latest, MeasurementHistoryStore history) { this.latest = latest; this.history = history; }
    public List<Measurement> latest(String projectId, String deviceId) { return latest.latestByDevice(projectId, deviceId); }
    public Measurement latest(String projectId, String deviceId, String channelId) { return latest.get(projectId, deviceId, channelId).orElse(null); }
    public List<Measurement> query(String projectId, String deviceId, String channelId, Instant from, Instant to, int limit) {
        return history.query(projectId, deviceId, channelId, from, to, limit);
    }
}
