package com.xuyuchen.health.alert;

import java.util.List;
import java.util.Optional;

public interface AlertStateRepository {
    AlertStateMachine save(AlertStateMachine machine);
    Optional<AlertStateMachine> find(String fingerprint);
    List<AlertStateMachine> listByProject(String projectId);
}
