package com.xuyuchen.health.measurement;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Repository;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Repository
@ConditionalOnProperty(name = "health.latest-store", havingValue = "redis")
public class RedisLatestMeasurementStore implements LatestMeasurementStore {
    private static final Duration ENTRY_TTL = Duration.ofHours(24);
    private static final DefaultRedisScript<Long> PUT_IF_NEWER = new DefaultRedisScript<>("""
            local currentTime = redis.call('HGET', KEYS[1], 'eventTimeEpochMillis')
            local currentSequence = redis.call('HGET', KEYS[1], 'sequence')
            local incomingTime = tonumber(ARGV[2])
            local incomingSequence = tonumber(ARGV[3])
            if currentTime and currentSequence then
                local storedTime = tonumber(currentTime)
                local storedSequence = tonumber(currentSequence)
                if storedTime > incomingTime or (storedTime == incomingTime and storedSequence >= incomingSequence) then
                    return 0
                end
            end
            redis.call('HSET', KEYS[1], 'payload', ARGV[1], 'eventTimeEpochMillis', ARGV[2], 'sequence', ARGV[3])
            redis.call('EXPIRE', KEYS[1], ARGV[4])
            return 1
            """, Long.class);

    private final StringRedisTemplate redis;
    private final ObjectMapper mapper;
    public RedisLatestMeasurementStore(StringRedisTemplate redis, ObjectMapper mapper) { this.redis = redis; this.mapper = mapper; }
    private String encode(String value) { return Base64.getUrlEncoder().withoutPadding().encodeToString(value.getBytes(StandardCharsets.UTF_8)); }
    private String prefix(String p, String d) { return "health:latest:v2:" + encode(p) + ":" + encode(d) + ":"; }
    private String key(String p, String d, String c) { return prefix(p, d) + encode(c); }
    @Override public boolean putIfNewer(Measurement measurement) {
        try {
            String key = key(measurement.projectId(), measurement.deviceId(), measurement.channelId());
            String payload = mapper.writeValueAsString(measurement);
            Long result = redis.execute(PUT_IF_NEWER, List.of(key), payload,
                    String.valueOf(measurement.eventTime().toEpochMilli()), String.valueOf(measurement.sequence()), String.valueOf(ENTRY_TTL.toSeconds()));
            return Long.valueOf(1L).equals(result);
        } catch (Exception ex) { throw new IllegalStateException("redis latest store failed", ex); }
    }
    @Override public Optional<Measurement> get(String p, String d, String c) {
        try { Object value = redis.opsForHash().get(key(p, d, c), "payload"); return value == null ? Optional.empty() : Optional.of(mapper.readValue(String.valueOf(value), Measurement.class)); }
        catch (Exception ex) { throw new IllegalStateException("redis latest read failed", ex); }
    }
    @Override public List<Measurement> latestByDevice(String p, String d) {
        List<Measurement> result = new ArrayList<>();
        try (Cursor<String> cursor = redis.scan(ScanOptions.scanOptions().match(prefix(p, d) + "*").count(256).build())) {
            while (cursor.hasNext()) {
                Object value = redis.opsForHash().get(cursor.next(), "payload");
                if (value != null) result.add(mapper.readValue(String.valueOf(value), Measurement.class));
            }
            return result;
        } catch (Exception ex) { throw new IllegalStateException("redis latest list failed", ex); }
    }
}
