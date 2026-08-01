package com.xuyuchen.health.alert;

import com.xuyuchen.health.common.TraceFilter;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AlertService {
    private final AlertRuleService rules;
    private final AlertRepository alerts;
    private final AlertStateRepository states;
    private final AlertEventPublisher publisher;
    private final Map<String, Object> locks = new ConcurrentHashMap<>();
    public AlertService(AlertRuleService rules, AlertRepository alerts, AlertStateRepository states, AlertEventPublisher publisher) {
        this.rules = rules; this.alerts = alerts; this.states = states; this.publisher = publisher;
    }
    public AlertEvent evaluate(String projectId, String deviceId, String channelId, double value, Instant eventTime) {
        for (AlertRule rule : rules.byChannel(projectId, channelId)) evaluateRule(rule, deviceId, value, eventTime);
        return alerts.findByProject(projectId).stream().filter(e -> e.deviceId().equals(deviceId) && e.channelId().equals(channelId)).reduce((a, b) -> b).orElse(null);
    }
    private void evaluateRule(AlertRule rule, String deviceId, double value, Instant eventTime) {
        String fingerprint = AlertFingerprint.of(rule.projectId(), deviceId, rule.channelId(), rule.ruleId(), rule.version());
        synchronized (locks.computeIfAbsent(fingerprint, k -> new Object())) {
            AlertStateMachine state = states.find(fingerprint).orElseGet(() -> states.save(new AlertStateMachine(rule, deviceId)));
            AlertStatus before = state.status();
            AlertEvent event = state.evaluate(value, eventTime);
            states.save(state); alerts.save(event);
            if (before != event.status()) publisher.publish(event);
        }
    }
    public AlertEvent ack(String projectId, String fingerprint) {
        AlertEvent current = alerts.find(fingerprint).orElseThrow(() -> new IllegalArgumentException("alert not found"));
        if (!current.projectId().equals(projectId)) throw new IllegalArgumentException("alert not found");
        AlertStateMachine state = states.find(fingerprint).orElseThrow(() -> new IllegalArgumentException("alert state not found"));
        AlertEvent event = state.ack(); states.save(state); alerts.save(event); publisher.publish(event); return event;
    }
    public List<AlertEvent> list(String projectId) { return alerts.findByProject(projectId); }
    public List<AlertEvent> active(String projectId) { return alerts.findActive(projectId); }
}
