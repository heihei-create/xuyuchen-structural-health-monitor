package com.xuyuchen.health.alert;

import org.springframework.stereotype.Component;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
@ConditionalOnProperty(name = "health.messaging", havingValue = "memory", matchIfMissing = true)
public class InMemoryAlertEventPublisher implements AlertEventPublisher {
    private final CopyOnWriteArrayList<AlertEvent> events = new CopyOnWriteArrayList<>();
    @Override public void publish(AlertEvent event) { events.add(event); }
    public List<AlertEvent> recent(int limit) {
        int from = Math.max(0, events.size() - Math.max(1, limit));
        java.util.ArrayList<AlertEvent> result = new java.util.ArrayList<>(events.subList(from, events.size()));
        java.util.Collections.reverse(result); return result;
    }
    public List<AlertEvent> recent(String projectId, int limit) {
        return events.stream().filter(event -> event.projectId().equals(projectId))
                .sorted(java.util.Comparator.comparing(AlertEvent::lastSeenAt, java.util.Comparator.nullsLast(java.util.Comparator.reverseOrder())))
                .limit(Math.max(1, limit)).toList();
    }
}
