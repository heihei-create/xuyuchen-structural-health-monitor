package com.xuyuchen.health.measurement;

import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class MessageDeduplicationService {
    public record Reservation(String key, String token, boolean retryable) {
        public Reservation(String key, String token) { this(key, token, false); }
    }
    private record Seen(Instant expiresAt, String token, boolean committed, boolean retryable, boolean derivedUpdated) {}
    private final Map<String, Seen> seen = new ConcurrentHashMap<>();
    private final Duration ttl;
    private final Clock clock;

    public MessageDeduplicationService() {
        this(Duration.ofMinutes(10), Clock.systemUTC());
    }

    MessageDeduplicationService(Duration ttl, Clock clock) {
        if (ttl.isNegative() || ttl.isZero()) throw new IllegalArgumentException("dedup ttl must be positive");
        this.ttl = ttl;
        this.clock = clock;
    }

    public boolean first(String projectId, String messageId) {
        Reservation reservation = reserve(projectId, messageId);
        if (reservation == null) return false;
        commit(reservation);
        return true;
    }
    public Reservation reserve(String projectId, String messageId) {
        if (projectId == null || projectId.isBlank() || messageId == null || messageId.isBlank()) throw new IllegalArgumentException("dedup key is required");
        String key = projectId + ":" + messageId;
        Instant now = clock.instant();
        String token = java.util.UUID.randomUUID().toString();
        AtomicBoolean accepted = new AtomicBoolean(false);
        seen.compute(key, (ignored, previous) -> {
            if (previous == null || !previous.expiresAt().isAfter(now)) {
                accepted.set(true);
                return new Seen(now.plus(ttl), token, false, false, false);
            }
            if (previous.retryable()) {
                accepted.set(true);
                return new Seen(previous.expiresAt(), token, false, false, previous.derivedUpdated());
            }
            return previous;
        });
        if (!accepted.get()) return null;
        Seen current = seen.get(key);
        return new Reservation(key, token, current != null && current.derivedUpdated());
    }
    public void markDerived(Reservation reservation, boolean updated) { seen.computeIfPresent(reservation.key(), (key, value) -> value.token().equals(reservation.token()) ? new Seen(value.expiresAt(), value.token(), false, false, updated) : value); }
    public void commit(Reservation reservation) { seen.computeIfPresent(reservation.key(), (key, value) -> value.token().equals(reservation.token()) ? new Seen(value.expiresAt(), value.token(), true, false, value.derivedUpdated()) : value); }
    public void release(Reservation reservation) {
        seen.computeIfPresent(reservation.key(), (key, value) -> value.token().equals(reservation.token()) && !value.committed()
                ? new Seen(value.expiresAt(), "", false, true, value.derivedUpdated()) : value);
    }
    public int size() { return seen.size(); }

    @Scheduled(fixedDelayString = "${health.dedup-cleanup-delay-ms:60000}")
    public void cleanup() {
        Instant now = clock.instant();
        seen.entrySet().removeIf(e -> !e.getValue().expiresAt().isAfter(now));
    }
}
