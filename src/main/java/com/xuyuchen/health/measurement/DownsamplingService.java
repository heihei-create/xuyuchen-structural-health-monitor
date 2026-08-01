package com.xuyuchen.health.measurement;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class DownsamplingService {
    private final MeasurementHistoryStore history;
    public DownsamplingService(MeasurementHistoryStore history) { this.history = history; }
    public List<DownsamplePoint> downsample(String projectId, String deviceId, String channelId, Instant from, Instant to, int seconds, int limit) {
        if (seconds < 1 || seconds > 86400) throw new IllegalArgumentException("bucket seconds out of range");
        List<Measurement> records = history.query(projectId, deviceId, channelId, from, to, Math.min(limit * 100, 10000));
        Map<Long, Bucket> buckets = new LinkedHashMap<>();
        for (Measurement measurement : records) {
            long bucket = (measurement.eventTime().getEpochSecond() / seconds) * seconds;
            buckets.computeIfAbsent(bucket, key -> new Bucket(Instant.ofEpochSecond(key))).add(measurement);
        }
        return buckets.values().stream().sorted(Comparator.comparing(Bucket::time)).limit(Math.max(1, limit)).map(Bucket::point).toList();
    }
    private static final class Bucket {
        private final Instant time; private int count; private double sum; private double min = Double.POSITIVE_INFINITY; private double max = Double.NEGATIVE_INFINITY; private double last;
        private Bucket(Instant time) { this.time = time; }
        private Instant lastEventTime;
        private long lastSequence;
        private void add(Measurement measurement) {
            double value = measurement.value();
            count++; sum += value; min = Math.min(min, value); max = Math.max(max, value);
            if (lastEventTime == null || measurement.eventTime().isAfter(lastEventTime)
                    || (measurement.eventTime().equals(lastEventTime) && measurement.sequence() > lastSequence)) {
                last = value;
                lastEventTime = measurement.eventTime();
                lastSequence = measurement.sequence();
            }
        }
        private Instant time() { return time; }
        private DownsamplePoint point() { return new DownsamplePoint(time, count, min, max, sum / count, last); }
    }
}
