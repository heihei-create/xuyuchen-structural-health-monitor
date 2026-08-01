package com.xuyuchen.health.alert;

import org.springframework.stereotype.Service;

@Service
public class AlertSeverityService {
    public AlertSeverity classify(AlertRule rule, double value) {
        double distance = Math.max(value - rule.upperLimit(), rule.lowerLimit() - value);
        double range = Math.max(0.000001, rule.upperLimit() - rule.lowerLimit());
        if (distance > range) return AlertSeverity.CRITICAL;
        if (distance > range * 0.25) return AlertSeverity.WARNING;
        return AlertSeverity.INFO;
    }
}
