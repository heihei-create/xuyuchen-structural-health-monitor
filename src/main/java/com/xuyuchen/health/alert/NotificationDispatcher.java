package com.xuyuchen.health.alert;

public interface NotificationDispatcher {
    DeliveryResult send(AlertNotification notification);
    record DeliveryResult(boolean success, String providerId, String error) {}
}
