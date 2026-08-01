package com.xuyuchen.health.alert;

import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AlertSilenceService {
    private final Map<String, AlertSilence> silences = new ConcurrentHashMap<>();
    public AlertSilence silence(String projectId, String fingerprint, String operator, String reason, Duration duration) {
        if (duration.isNegative() || duration.isZero()) throw new IllegalArgumentException("silence duration must be positive");
        AlertSilence silence = new AlertSilence(projectId, fingerprint, operator, reason, Instant.now().plus(duration));
        silences.put(projectId + ":" + fingerprint, silence); return silence;
    }
    public boolean active(String projectId, String fingerprint) {
        AlertSilence silence = silences.get(projectId + ":" + fingerprint);
        return silence != null && silence.active();
    }
    public void remove(String projectId, String fingerprint) { silences.remove(projectId + ":" + fingerprint); }
}
