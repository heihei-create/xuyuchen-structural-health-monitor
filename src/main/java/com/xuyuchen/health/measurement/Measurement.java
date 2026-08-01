package com.xuyuchen.health.measurement;

import java.time.Instant;

public record Measurement(
        String projectId,
        String deviceId,
        String channelId,
        Instant eventTime,
        long sequence,
        double value,
        String unit,
        String messageId
) {}
