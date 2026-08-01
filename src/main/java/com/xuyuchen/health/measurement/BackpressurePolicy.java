package com.xuyuchen.health.measurement;

public record BackpressurePolicy(int queueLimit, boolean dropOldest, boolean sampleLatest) {
    public BackpressurePolicy {
        if (queueLimit < 1) throw new IllegalArgumentException("queue limit must be positive");
    }
    public static BackpressurePolicy defaultPolicy() { return new BackpressurePolicy(10000, true, true); }
}
