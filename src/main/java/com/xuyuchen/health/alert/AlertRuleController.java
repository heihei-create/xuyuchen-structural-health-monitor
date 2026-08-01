package com.xuyuchen.health.alert;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.xuyuchen.health.alert.AlertRuleDtos.*;

@RestController
@RequestMapping("/api/v1/projects/{projectId}/rules")
public class AlertRuleController {
    private final AlertRuleService service;
    public AlertRuleController(AlertRuleService service) { this.service = service; }
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AlertRule create(@PathVariable String projectId, @Valid @RequestBody CreateRuleRequest req) {
        return service.create(projectId, req.ruleId(), req.channelId(), req.upperLimit(), req.lowerLimit(), req.consecutiveSamples(), req.recoverySamples(), req.version());
    }
    @GetMapping
    public List<AlertRule> list(@PathVariable String projectId) { return service.list(projectId); }
}
