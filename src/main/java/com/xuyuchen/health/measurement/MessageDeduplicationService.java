package com.xuyuchen.health.measurement;

import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class MessageDeduplicationService {
    private record Seen(Instant expiresAt) {}
    private final Map<String, Seen> seen = new ConcurrentHashMap<>();
    private final Duration ttl = Duration.ofMinutes(10);
    public boolean first(String projectId, String messageId) {
        String key = projectId + ":" + messageId;
        Instant now = Instant.now();
        Seen previous = seen.putIfAbsent(key, new Seen(now.plus(ttl)));
        if (previous == null) return true;
        if (previous.expiresAt().isBefore(now)) { seen.put(key, new Seen(now.plus(ttl))); return true; }
        return false;
    }
    public int size() { return seen.size(); }
    public void cleanup() { Instant now = Instant.now(); seen.entrySet().removeIf(e -> e.getValue().expiresAt().isBefore(now)); }
}
