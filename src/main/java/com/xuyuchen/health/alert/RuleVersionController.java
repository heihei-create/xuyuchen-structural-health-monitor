package com.xuyuchen.health.alert;

import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/v1/projects/{projectId}/rule-versions")
public class RuleVersionController {
    private final RuleVersionService service;
    public RuleVersionController(RuleVersionService service) { this.service = service; }
    @PostMapping("/{ruleId}")
    public AlertRule publish(@PathVariable String projectId, @PathVariable String ruleId, @Valid @RequestBody AlertRule rule) {
        if (!rule.projectId().equals(projectId) || !rule.ruleId().equals(ruleId)) throw new IllegalArgumentException("rule identity mismatch");
        return service.publish(rule);
    }
    @GetMapping("/{ruleId}")
    public List<AlertRule> history(@PathVariable String projectId, @PathVariable String ruleId) { return service.history(projectId, ruleId); }
}
