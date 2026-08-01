package com.xuyuchen.health.alert;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Repository
public class InMemoryAlertRepository implements AlertRepository {
    private final ConcurrentMap<String, AlertEvent> records = new ConcurrentHashMap<>();
    @Override public AlertEvent save(AlertEvent event) { records.put(event.fingerprint(), event); return event; }
    @Override public Optional<AlertEvent> find(String fingerprint) { return Optional.ofNullable(records.get(fingerprint)); }
    @Override public List<AlertEvent> findByProject(String projectId) { return records.values().stream().filter(e -> e.projectId().equals(projectId)).toList(); }
    @Override public List<AlertEvent> findActive(String projectId) { return records.values().stream().filter(e -> e.projectId().equals(projectId) && (e.status() == AlertStatus.TRIGGERED || e.status() == AlertStatus.ACKED)).toList(); }
}
