package com.xuyuchen.health.alert;

public record AlertRule(
        String projectId,
        String ruleId,
        String channelId,
        double upperLimit,
        double lowerLimit,
        int consecutiveSamples,
        int recoverySamples,
        int version
) {
    public boolean violates(double value) {
        return value > upperLimit || value < lowerLimit;
    }
}
