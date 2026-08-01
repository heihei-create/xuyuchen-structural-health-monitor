package com.xuyuchen.health.alert;

import java.time.Instant;

public record AlertEvent(
        String fingerprint,
        String projectId,
        String ruleId,
        String deviceId,
        String channelId,
        AlertStatus status,
        double lastValue,
        int ruleVersion,
        int violationCount,
        int recoveryCount,
        Instant firstTriggeredAt,
        Instant lastSeenAt
) {
    AlertEvent withStatus(AlertStatus next, int violations, int recoveries, double value, Instant time) {
        return new AlertEvent(fingerprint, projectId, ruleId, deviceId, channelId, next, value, ruleVersion, violations, recoveries, firstTriggeredAt, time);
    }
}
