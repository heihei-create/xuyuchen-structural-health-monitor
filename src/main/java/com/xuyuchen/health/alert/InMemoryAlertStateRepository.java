package com.xuyuchen.health.alert;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Repository
public class InMemoryAlertStateRepository implements AlertStateRepository {
    private final ConcurrentMap<String, AlertStateMachine> states = new ConcurrentHashMap<>();
    @Override public AlertStateMachine save(AlertStateMachine machine) { states.put(machine.fingerprint(), machine); return machine; }
    @Override public Optional<AlertStateMachine> find(String fingerprint) { return Optional.ofNullable(states.get(fingerprint)); }
    @Override public List<AlertStateMachine> listByProject(String projectId) {
        return states.values().stream().filter(s -> s.snapshot().projectId().equals(projectId)).toList();
    }
}
