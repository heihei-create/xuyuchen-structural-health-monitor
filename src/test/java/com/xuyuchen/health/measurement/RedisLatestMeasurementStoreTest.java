package com.xuyuchen.health.measurement;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class RedisLatestMeasurementStoreTest {
    @Test
    @SuppressWarnings({"unchecked", "rawtypes"})
    void latestByDeviceScansOnlyDeviceKeysAndReadsPayloadHash() throws Exception {
        StringRedisTemplate redis = mock(StringRedisTemplate.class);
        HashOperations hash = mock(HashOperations.class);
        Cursor<String> cursor = mock(Cursor.class);
        ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();
        Measurement first = measurement("c1", 1, 10);
        Measurement second = measurement("c2", 2, 20);

        when(redis.opsForHash()).thenReturn(hash);
        when(redis.scan(any(ScanOptions.class))).thenReturn(cursor);
        when(cursor.hasNext()).thenReturn(true, true, false);
        when(cursor.next()).thenReturn("health:latest:v2:p:d:c1", "health:latest:v2:p:d:c2");
        when(hash.get(any(), eq("payload")))
                .thenReturn(mapper.writeValueAsString(first), mapper.writeValueAsString(second));

        List<Measurement> result = new RedisLatestMeasurementStore(redis, mapper).latestByDevice("p", "d");

        assertEquals(List.of(first, second), result);
        verify(redis).scan(any(ScanOptions.class));
        verify(hash, times(2)).get(any(), eq("payload"));
    }

    @Test
    void putIfNewerDelegatesEventTimeAndSequenceComparisonToLuaAtomically() throws Exception {
        StringRedisTemplate redis = mock(StringRedisTemplate.class);
        ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();
        Measurement measurement = measurement("c1", 42, 10);
        String payload = mapper.writeValueAsString(measurement);
        when(redis.execute(any(RedisScript.class), anyList(), any(Object[].class))).thenReturn(1L);

        assertTrue(new RedisLatestMeasurementStore(redis, mapper).putIfNewer(measurement));

        verify(redis).execute(any(RedisScript.class), anyList(),
                eq(payload), eq(String.valueOf(measurement.eventTime().toEpochMilli())), eq("42"), eq("86400"));
    }

    private static Measurement measurement(String channel, long sequence, double value) {
        return new Measurement("p", "d", channel, Instant.parse("2026-08-01T00:00:10Z"), sequence, value, "mm", "m-" + sequence);
    }
}
