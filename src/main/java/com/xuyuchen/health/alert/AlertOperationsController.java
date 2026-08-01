package com.xuyuchen.health.alert;

import jakarta.validation.constraints.NotBlank;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/projects/{projectId}/alert-operations")
public class AlertOperationsController {
    private final AlertSilenceService silence;
    private final AlertNotificationService notifications;
    public AlertOperationsController(AlertSilenceService silence, AlertNotificationService notifications) { this.silence = silence; this.notifications = notifications; }
    public record SilenceRequest(@NotBlank String operator, @NotBlank String reason, long durationSeconds) {}
    @PostMapping("/{fingerprint}/silence")
    public AlertSilence silence(@PathVariable String projectId, @PathVariable String fingerprint, @RequestBody SilenceRequest req) {
        return silence.silence(projectId, fingerprint, req.operator(), req.reason(), Duration.ofSeconds(Math.max(1, req.durationSeconds())));
    }
    @DeleteMapping("/{fingerprint}/silence")
    public void unsilence(@PathVariable String projectId, @PathVariable String fingerprint) { silence.remove(projectId, fingerprint); }
    @PostMapping("/{fingerprint}/notification")
    public AlertNotification notify(@PathVariable String fingerprint, @RequestParam String channel, @RequestParam String recipient) {
        return notifications.enqueue(new AlertEvent(fingerprint, "unknown", "manual", "unknown", "unknown", AlertStatus.TRIGGERED, 0, 1, 1, 1, java.time.Instant.now(), java.time.Instant.now()), channel, recipient);
    }
}
