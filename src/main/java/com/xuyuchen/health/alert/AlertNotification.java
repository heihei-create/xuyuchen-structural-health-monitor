package com.xuyuchen.health.alert;

import java.time.Instant;
import java.util.UUID;

public record AlertNotification(UUID id, String fingerprint, String channel, String recipient, String message, int attempts, Instant createdAt, Instant sentAt, String lastError) {
    public AlertNotification retry(String error) { return new AlertNotification(id, fingerprint, channel, recipient, message, attempts + 1, createdAt, null, error); }
    public AlertNotification sent() { return new AlertNotification(id, fingerprint, channel, recipient, message, attempts, createdAt, Instant.now(), null); }
}
