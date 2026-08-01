package com.xuyuchen.health.alert;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class NotificationDeliveryService {
    private final NotificationRepository notifications;
    private final NotificationDispatcher dispatcher;
    public NotificationDeliveryService(NotificationRepository notifications, NotificationDispatcher dispatcher) { this.notifications = notifications; this.dispatcher = dispatcher; }
    @Scheduled(fixedDelayString = "${health.notification-delay-ms:10000}")
    public void deliver() { process(100); }
    public int process(int limit) {
        int delivered = 0;
        for (AlertNotification notification : notifications.pending(Math.min(Math.max(1, limit), 1000))) {
            NotificationDispatcher.DeliveryResult result = dispatcher.send(notification);
            if (result.success()) { notifications.save(notification.sent()); delivered++; }
            else notifications.save(notification.retry(result.error()));
        }
        return delivered;
    }
}
