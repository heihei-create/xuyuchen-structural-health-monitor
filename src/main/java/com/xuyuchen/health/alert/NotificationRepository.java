package com.xuyuchen.health.alert;

import java.util.List;

public interface NotificationRepository {
    AlertNotification save(AlertNotification notification);
    List<AlertNotification> pending(int limit);
    List<AlertNotification> listByFingerprint(String fingerprint);
}
