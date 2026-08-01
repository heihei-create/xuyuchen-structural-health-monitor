package com.xuyuchen.health.measurement;

import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.*;

class MessageDeduplicationServiceTest {
    @Test
    void acceptsOnlyOneMessageUntilTtlExpires() {
        MutableClock clock = new MutableClock(Instant.parse("2026-08-01T00:00:00Z"));
        MessageDeduplicationService service = new MessageDeduplicationService(Duration.ofMinutes(10), clock);

        assertTrue(service.first("p1", "m1"));
        assertFalse(service.first("p1", "m1"));
        assertEquals(1, service.size());

        clock.advance(Duration.ofMinutes(10));
        service.cleanup();
        assertEquals(0, service.size());
        assertTrue(service.first("p1", "m1"));
    }

    @Test
    void deduplicationIsScopedByProject() {
        MessageDeduplicationService service = new MessageDeduplicationService();

        assertTrue(service.first("p1", "m1"));
        assertTrue(service.first("p2", "m1"));
        assertFalse(service.first("p1", "m1"));
    }

    @Test
    void failedReservationCanBeRetried() {
        MessageDeduplicationService service = new MessageDeduplicationService();
        MessageDeduplicationService.Reservation reservation = service.reserve("p1", "m1");
        assertNotNull(reservation);
        assertFalse(service.first("p1", "m1"));
        service.release(reservation);
        assertTrue(service.first("p1", "m1"));
    }

    private static final class MutableClock extends Clock {
        private Instant current;

        private MutableClock(Instant current) { this.current = current; }
        private void advance(Duration duration) { current = current.plus(duration); }
        @Override public ZoneOffset getZone() { return ZoneOffset.UTC; }
        @Override public Clock withZone(java.time.ZoneId zone) { return this; }
        @Override public Instant instant() { return current; }
    }
}
