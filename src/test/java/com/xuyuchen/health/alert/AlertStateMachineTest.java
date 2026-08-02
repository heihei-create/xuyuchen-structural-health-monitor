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

    @Test
    void duplicateOrLateSequencedMeasurementDoesNotAdvanceDebounceState() {
        AlertStateMachine machine = new AlertStateMachine(rule, "d");
        Instant first = Instant.parse("2026-08-01T00:00:01Z");
        machine.evaluate(101, first, 10);

        AlertEvent duplicate = machine.evaluate(102, first, 10);
        assertEquals(1, duplicate.violationCount());
        assertEquals(AlertStatus.NORMAL, duplicate.status());

        AlertEvent late = machine.evaluate(102, Instant.parse("2026-08-01T00:00:00Z"), 9);
        assertEquals(1, late.violationCount());
        assertEquals(AlertStatus.NORMAL, late.status());

        AlertEvent next = machine.evaluate(102, Instant.parse("2026-08-01T00:00:02Z"), 11);
        assertEquals(AlertStatus.TRIGGERED, next.status());
    }
}
