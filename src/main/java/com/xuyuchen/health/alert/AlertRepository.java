package com.xuyuchen.health.alert;

import java.util.List;
import java.util.Optional;

public interface AlertRepository {
    AlertEvent save(AlertEvent event);
    Optional<AlertEvent> find(String fingerprint);
    List<AlertEvent> findByProject(String projectId);
    List<AlertEvent> findActive(String projectId);
}
