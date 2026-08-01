package com.xuyuchen.health.alert;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class InMemoryAlertEventPublisher implements AlertEventPublisher {
    private final CopyOnWriteArrayList<AlertEvent> events = new CopyOnWriteArrayList<>();
    @Override public void publish(AlertEvent event) { events.add(event); }
    public List<AlertEvent> recent(int limit) {
        int from = Math.max(0, events.size() - Math.max(1, limit));
        java.util.ArrayList<AlertEvent> result = new java.util.ArrayList<>(events.subList(from, events.size()));
        java.util.Collections.reverse(result); return result;
    }
}
