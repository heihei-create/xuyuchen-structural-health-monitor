package com.xuyuchen.health.device;

public record DeviceChannel(String projectId, String deviceId, String channelId, String metric, String unit, double minValue, double maxValue, int sampleIntervalSeconds) {
    public boolean valid(double value) { return Double.isFinite(value) && value >= minValue && value <= maxValue; }
}
