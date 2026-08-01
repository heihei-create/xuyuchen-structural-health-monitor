package com.xuyuchen.health.alert;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RuleVersionService {
    private final Map<String, Map<Integer, AlertRule>> versions = new ConcurrentHashMap<>();
    public AlertRule publish(AlertRule rule) {
        versions.computeIfAbsent(rule.projectId() + ":" + rule.ruleId(), key -> new ConcurrentHashMap<>()).put(rule.version(), rule);
        return rule;
    }
    public AlertRule get(String projectId, String ruleId, int version) {
        Map<Integer, AlertRule> values = versions.get(projectId + ":" + ruleId);
        if (values == null || !values.containsKey(version)) throw new IllegalArgumentException("rule version not found");
        return values.get(version);
    }
    public List<AlertRule> history(String projectId, String ruleId) {
        Map<Integer, AlertRule> values = versions.getOrDefault(projectId + ":" + ruleId, Map.of());
        return values.values().stream().sorted(java.util.Comparator.comparingInt(AlertRule::version).reversed()).toList();
    }
}
