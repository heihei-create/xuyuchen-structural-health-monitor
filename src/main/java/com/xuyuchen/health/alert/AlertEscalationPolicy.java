package com.xuyuchen.health.alert;

import java.time.Duration;
import java.util.List;

public record AlertEscalationPolicy(String projectId, String ruleId, Duration firstDelay, List<NotificationChannel> channels, String recipient) {
    public AlertEscalationPolicy {
        if (firstDelay.isNegative() || channels == null || channels.isEmpty()) throw new IllegalArgumentException("invalid escalation policy");
    }
}
