package com.xuyuchen.health.measurement;

import java.time.Instant;

public record LatestValueProjection(String projectId, String deviceId, String channelId, double value, String unit, Instant eventTime, long sequence, boolean stale) {}
