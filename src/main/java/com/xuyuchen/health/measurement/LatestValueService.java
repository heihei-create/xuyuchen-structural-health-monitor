package com.xuyuchen.health.measurement;

import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Service
public class LatestValueService {
    private final LatestMeasurementStore latest;
    private final Duration staleAfter = Duration.ofMinutes(5);
    public LatestValueService(LatestMeasurementStore latest) { this.latest = latest; }
    public LatestValueProjection get(String p, String d, String c) {
        Measurement m = latest.get(p, d, c).orElseThrow(() -> new IllegalArgumentException("latest value not found"));
        return new LatestValueProjection(p, d, c, m.value(), m.unit(), m.eventTime(), m.sequence(), m.eventTime().plus(staleAfter).isBefore(Instant.now()));
    }
    public List<LatestValueProjection> list(String p, String d) { return latest.latestByDevice(p, d).stream().map(m -> new LatestValueProjection(p, d, m.channelId(), m.value(), m.unit(), m.eventTime(), m.sequence(), m.eventTime().plus(staleAfter).isBefore(Instant.now()))).toList(); }
}
