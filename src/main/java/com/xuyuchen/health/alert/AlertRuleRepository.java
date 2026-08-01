package com.xuyuchen.health.alert;

import java.util.List;
import java.util.Optional;

public interface AlertRuleRepository {
    AlertRule save(AlertRule rule);
    Optional<AlertRule> find(String projectId, String ruleId);
    List<AlertRule> findByProject(String projectId);
    List<AlertRule> findByChannel(String projectId, String channelId);
}
