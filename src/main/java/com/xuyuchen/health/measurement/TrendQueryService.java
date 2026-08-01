package com.xuyuchen.health.measurement;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class TrendQueryService {
    private final DownsamplingService downsampling;
    private final MeasurementQueryService measurements;
    public TrendQueryService(DownsamplingService downsampling, MeasurementQueryService measurements) { this.downsampling = downsampling; this.measurements = measurements; }
    public TrendResult query(String p, String d, String c, Instant from, Instant to, int bucket, int limit) {
        List<DownsamplePoint> points = downsampling.downsample(p, d, c, from, to, bucket, limit);
        return new TrendResult(p, d, c, from, to, bucket, points, points.stream().mapToDouble(DownsamplePoint::average).average().orElse(0));
    }
    public List<Measurement> raw(String p, String d, String c, Instant from, Instant to, int limit) { return measurements.query(p, d, c, from, to, limit); }
    public record TrendResult(String projectId, String deviceId, String channelId, Instant from, Instant to, int bucketSeconds, List<DownsamplePoint> points, double average) {}
}
