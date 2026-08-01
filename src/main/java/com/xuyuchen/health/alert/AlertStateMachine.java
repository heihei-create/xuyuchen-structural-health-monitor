package com.xuyuchen.health.alert;

import java.time.Instant;

public class AlertStateMachine {
    private final AlertRule rule;
    private final String fingerprint;
    private final String projectId;
    private final String deviceId;
    private final String channelId;
    private AlertStatus status = AlertStatus.NORMAL;
    private int violations;
    private int recoveries;
    private Instant firstTriggeredAt;
    private Instant lastSeenAt;
    private double lastValue;
    public AlertStateMachine(AlertRule rule, String deviceId) {
        this.rule = rule; this.deviceId = deviceId; this.projectId = rule.projectId(); this.channelId = rule.channelId();
        this.fingerprint = AlertFingerprint.of(projectId, deviceId, channelId, rule.ruleId(), rule.version());
    }
    public synchronized AlertEvent evaluate(double value, Instant eventTime) {
        lastValue = value; lastSeenAt = eventTime;
        if (rule.violates(value)) {
            violations++; recoveries = 0;
            if (status == AlertStatus.NORMAL || status == AlertStatus.RECOVERED) {
                if (violations >= rule.consecutiveSamples()) { status = AlertStatus.TRIGGERED; firstTriggeredAt = eventTime; }
            }
        } else {
            violations = 0;
            if (status == AlertStatus.TRIGGERED || status == AlertStatus.ACKED) {
                recoveries++;
                if (recoveries >= rule.recoverySamples()) status = AlertStatus.RECOVERED;
            } else recoveries = 0;
        }
        return snapshot();
    }
    public synchronized AlertEvent ack() {
        if (status != AlertStatus.TRIGGERED) throw new IllegalStateException("only triggered alert can be acknowledged");
        status = AlertStatus.ACKED; return snapshot();
    }
    public synchronized AlertEvent snapshot() {
        return new AlertEvent(fingerprint, projectId, rule.ruleId(), deviceId, channelId, status, lastValue, rule.version(), violations, recoveries, firstTriggeredAt, lastSeenAt);
    }
    public String fingerprint() { return fingerprint; }
    public AlertStatus status() { return status; }
    public synchronized AlertStateMachine copy() {
        AlertStateMachine copy = new AlertStateMachine(rule, deviceId);
        copy.status = status; copy.violations = violations; copy.recoveries = recoveries;
        copy.firstTriggeredAt = firstTriggeredAt; copy.lastSeenAt = lastSeenAt; copy.lastValue = lastValue;
        return copy;
    }
}
