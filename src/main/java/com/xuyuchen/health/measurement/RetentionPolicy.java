package com.xuyuchen.health.measurement;

import java.time.Duration;

public record RetentionPolicy(Duration rawRetention, Duration aggregateRetention, int downsampleSeconds) {
    public RetentionPolicy {
        if (rawRetention.isNegative() || aggregateRetention.isNegative() || downsampleSeconds < 1) throw new IllegalArgumentException("invalid retention policy");
    }
}
