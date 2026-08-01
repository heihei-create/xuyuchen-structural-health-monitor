package com.xuyuchen.health.alert;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/projects/{projectId}/alerts")
public class AlertController {
    private final AlertService service;
    public AlertController(AlertService service) { this.service = service; }
    @GetMapping
    public List<AlertEvent> list(@PathVariable String projectId, @RequestParam(defaultValue = "false") boolean active) { return active ? service.active(projectId) : service.list(projectId); }
    @PostMapping("/{fingerprint}/ack")
    public AlertEvent ack(@PathVariable String projectId, @PathVariable String fingerprint) { return service.ack(projectId, fingerprint); }
    @GetMapping("/events/recent")
    public List<AlertEvent> recent(@PathVariable String projectId, @RequestParam(defaultValue = "50") int limit) { return service.list(projectId).stream().limit(Math.min(Math.max(1, limit), 200)).toList(); }
}
