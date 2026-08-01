package com.xuyuchen.health.alert;

import java.time.Instant;

public record AlertSilence(String projectId, String fingerprint, String operator, String reason, Instant expiresAt) {
    public boolean active() { return expiresAt.isAfter(Instant.now()); }
}
