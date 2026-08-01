package com.xuyuchen.health.measurement;

import java.time.Instant;

public class RateLimitBucket {
    private final long capacity;
    private final double refillPerSecond;
    private double tokens;
    private Instant updatedAt;
    public RateLimitBucket(long capacity, double refillPerSecond) { this.capacity = capacity; this.refillPerSecond = refillPerSecond; this.tokens = capacity; this.updatedAt = Instant.now(); }
    public synchronized boolean acquire(long count) {
        refill();
        if (count > tokens) return false;
        tokens -= count; return true;
    }
    public synchronized double available() { refill(); return tokens; }
    private void refill() {
        Instant now = Instant.now();
        double seconds = (now.toEpochMilli() - updatedAt.toEpochMilli()) / 1000.0;
        tokens = Math.min(capacity, tokens + seconds * refillPerSecond); updatedAt = now;
    }
}
