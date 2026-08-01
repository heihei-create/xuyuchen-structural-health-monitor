package com.xuyuchen.health.measurement;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

@Repository
@ConditionalOnProperty(name = "health.latest-store", havingValue = "redis")
public class RedisLatestMeasurementStore implements LatestMeasurementStore {
    private final StringRedisTemplate redis;
    private final ObjectMapper mapper;
    public RedisLatestMeasurementStore(StringRedisTemplate redis, ObjectMapper mapper) { this.redis = redis; this.mapper = mapper; }
    private String key(String p, String d, String c) { return "health:latest:" + p + ":" + d + ":" + c; }
    @Override public boolean putIfNewer(Measurement measurement) {
        try {
            String key = key(measurement.projectId(), measurement.deviceId(), measurement.channelId());
            String current = redis.opsForValue().get(key);
            if (current != null && mapper.readValue(current, Measurement.class).eventTime().isAfter(measurement.eventTime())) return false;
            redis.opsForValue().set(key, mapper.writeValueAsString(measurement), Duration.ofHours(24)); return true;
        } catch (Exception ex) { throw new IllegalStateException("redis latest store failed", ex); }
    }
    @Override public Optional<Measurement> get(String p, String d, String c) {
        try { String value = redis.opsForValue().get(key(p, d, c)); return value == null ? Optional.empty() : Optional.of(mapper.readValue(value, Measurement.class)); }
        catch (Exception ex) { throw new IllegalStateException("redis latest read failed", ex); }
    }
    @Override public List<Measurement> latestByDevice(String p, String d) { return List.of(); }
}
