package com.xuyuchen.health.alert;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Repository
public class InMemoryAlertRuleRepository implements AlertRuleRepository {
    private final ConcurrentMap<String, AlertRule> rules = new ConcurrentHashMap<>();
    private String key(String projectId, String ruleId) { return projectId + ":" + ruleId; }
    @Override public AlertRule save(AlertRule rule) { rules.put(key(rule.projectId(), rule.ruleId()), rule); return rule; }
    @Override public Optional<AlertRule> find(String projectId, String ruleId) { return Optional.ofNullable(rules.get(key(projectId, ruleId))); }
    @Override public List<AlertRule> findByProject(String projectId) { return rules.values().stream().filter(r -> r.projectId().equals(projectId)).toList(); }
    @Override public List<AlertRule> findByChannel(String projectId, String channelId) { return rules.values().stream().filter(r -> r.projectId().equals(projectId) && r.channelId().equals(channelId)).toList(); }
}
