package com.xuyuchen.health.alert;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AlertStateMachineTest {
    private final AlertRule rule = new AlertRule("p", "r", "c", 100, -100, 2, 2, 1);

    @Test
    void triggersAfterConsecutiveViolations() {
        AlertStateMachine machine = new AlertStateMachine(rule, "d");
        machine.evaluate(101, Instant.parse("2026-08-01T00:00:01Z"));
        AlertEvent event = machine.evaluate(102, Instant.parse("2026-08-01T00:00:02Z"));

        assertEquals(AlertStatus.TRIGGERED, event.status());
        assertEquals(2, event.violationCount());
    }

    @Test
    void recoversAfterConsecutiveNormalValues() {
        AlertStateMachine machine = new AlertStateMachine(rule, "d");
        machine.evaluate(101, Instant.parse("2026-08-01T00:00:01Z"));
        machine.evaluate(102, Instant.parse("2026-08-01T00:00:02Z"));
        machine.evaluate(0, Instant.parse("2026-08-01T00:00:03Z"));
        AlertEvent event = machine.evaluate(0, Instant.parse("2026-08-01T00:00:04Z"));

        assertEquals(AlertStatus.RECOVERED, event.status());
        assertEquals(2, event.recoveryCount());
    }

    @Test
    void acknowledgementRequiresTriggeredState() {
        AlertStateMachine machine = new AlertStateMachine(rule, "d");

        assertThrows(IllegalStateException.class, machine::ack);
    }
}
