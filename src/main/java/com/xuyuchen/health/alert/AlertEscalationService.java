package com.xuyuchen.health.alert;

import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AlertEscalationService {
    private final Map<String, AlertEscalationPolicy> policies = new ConcurrentHashMap<>();
    private final AlertNotificationService notifications;
    public AlertEscalationService(AlertNotificationService notifications) { this.notifications = notifications; }
    public AlertEscalationPolicy configure(AlertEscalationPolicy policy) { policies.put(policy.projectId() + ":" + policy.ruleId(), policy); return policy; }
    public AlertEscalationPolicy policy(String projectId, String ruleId) { return policies.get(projectId + ":" + ruleId); }
    public int notify(AlertEvent event) {
        AlertEscalationPolicy policy = policy(event.projectId(), event.ruleId());
        if (policy == null) return 0;
        int count = 0;
        for (NotificationChannel channel : policy.channels()) if (notifications.enqueue(event, channel.name(), policy.recipient()) != null) count++;
        return count;
    }
}
