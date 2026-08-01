package com.xuyuchen.health.alert;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
public class AlertNotificationService {
    private final NotificationRepository notifications;
    private final AlertSilenceService silences;
    public AlertNotificationService(NotificationRepository notifications, AlertSilenceService silences) { this.notifications = notifications; this.silences = silences; }
    public AlertNotification enqueue(AlertEvent event, String channel, String recipient) {
        if (silences.active(event.projectId(), event.fingerprint())) return null;
        String message = "Alert " + event.ruleId() + " on " + event.deviceId() + "/" + event.channelId() + ": " + event.lastValue();
        return notifications.save(new AlertNotification(UUID.randomUUID(), event.fingerprint(), channel, recipient, message, 0, java.time.Instant.now(), null, null));
    }
    public int pending() { return notifications.pending(1000).size(); }
    public Map<String, Object> deliveryStatus(String fingerprint) { return Map.of("fingerprint", fingerprint, "notifications", notifications.listByFingerprint(fingerprint)); }
}
