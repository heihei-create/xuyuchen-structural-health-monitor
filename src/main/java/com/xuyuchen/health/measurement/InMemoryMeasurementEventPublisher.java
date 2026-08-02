package com.xuyuchen.health.measurement;

import org.springframework.stereotype.Component;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
@ConditionalOnProperty(name = "health.messaging", havingValue = "memory", matchIfMissing = true)
public class InMemoryMeasurementEventPublisher {
    private final CopyOnWriteArrayList<Measurement> events = new CopyOnWriteArrayList<>();
    private final Set<String> messageIds = ConcurrentHashMap.newKeySet();
    public void publish(Measurement measurement) { if (messageIds.add(measurement.projectId() + ":" + measurement.messageId())) events.add(measurement); }
    public List<Measurement> recent(int limit) {
        int from = Math.max(0, events.size() - Math.max(1, limit));
        java.util.ArrayList<Measurement> result = new java.util.ArrayList<>(events.subList(from, events.size()));
        java.util.Collections.reverse(result); return result;
    }
}
