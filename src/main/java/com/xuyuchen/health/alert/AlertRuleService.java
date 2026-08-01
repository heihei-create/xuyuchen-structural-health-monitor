package com.xuyuchen.health.alert;

import com.xuyuchen.health.device.ProjectAccessService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AlertRuleService {
    private final AlertRuleRepository rules;
    private final ProjectAccessService access;
    public AlertRuleService(AlertRuleRepository rules, ProjectAccessService access) { this.rules = rules; this.access = access; }
    public AlertRule create(String projectId, String ruleId, String channelId, double upper, double lower, int consecutive, int recovery, int version) {
        access.require(projectId);
        if (upper <= lower) throw new IllegalArgumentException("upper limit must be greater than lower limit");
        if (consecutive < 1 || recovery < 1) throw new IllegalArgumentException("sample counts must be positive");
        if (rules.find(projectId, ruleId).isPresent()) throw new IllegalStateException("rule already exists");
        return rules.save(new AlertRule(projectId, ruleId, channelId, upper, lower, consecutive, recovery, version));
    }
    public AlertRule get(String projectId, String ruleId) { return rules.find(projectId, ruleId).orElseThrow(() -> new IllegalArgumentException("rule not found")); }
    public List<AlertRule> list(String projectId) { access.require(projectId); return rules.findByProject(projectId); }
    public List<AlertRule> byChannel(String projectId, String channelId) { return rules.findByChannel(projectId, channelId); }
}
