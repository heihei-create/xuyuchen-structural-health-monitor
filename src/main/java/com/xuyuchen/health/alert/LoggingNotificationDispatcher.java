package com.xuyuchen.health.alert;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class LoggingNotificationDispatcher implements NotificationDispatcher {
    @Override public DeliveryResult send(AlertNotification notification) {
        if (notification.recipient() == null || notification.recipient().isBlank()) return new DeliveryResult(false, null, "recipient is empty");
        return new DeliveryResult(true, UUID.randomUUID().toString(), null);
    }
}
