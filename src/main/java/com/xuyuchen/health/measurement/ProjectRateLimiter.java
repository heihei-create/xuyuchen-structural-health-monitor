package com.xuyuchen.health.measurement;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ProjectRateLimiter {
    private final Map<String, RateLimitBucket> buckets = new ConcurrentHashMap<>();
    public void configure(String projectId, long capacity, double refillPerSecond) {
        if (capacity < 1 || refillPerSecond <= 0) throw new IllegalArgumentException("invalid rate limit");
        buckets.put(projectId, new RateLimitBucket(capacity, refillPerSecond));
    }
    public boolean allow(String projectId, long count) { return buckets.computeIfAbsent(projectId, id -> new RateLimitBucket(10000, 10000)).acquire(count); }
    public double available(String projectId) { return buckets.computeIfAbsent(projectId, id -> new RateLimitBucket(10000, 10000)).available(); }
}
