package com.xuyuchen.health.alert;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Repository
public class InMemoryNotificationRepository implements NotificationRepository {
    private final CopyOnWriteArrayList<AlertNotification> records = new CopyOnWriteArrayList<>();
    @Override public AlertNotification save(AlertNotification value) { records.removeIf(n -> n.id().equals(value.id())); records.add(value); return value; }
    @Override public List<AlertNotification> pending(int limit) { return records.stream().filter(n -> n.sentAt() == null).limit(Math.max(1, limit)).toList(); }
    @Override public List<AlertNotification> listByFingerprint(String fingerprint) { return records.stream().filter(n -> n.fingerprint().equals(fingerprint)).toList(); }
}
