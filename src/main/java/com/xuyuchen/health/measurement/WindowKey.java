package com.xuyuchen.health.measurement;

public record WindowKey(String projectId, String deviceId, String channelId, long windowStartEpochSecond, int seconds) {}
