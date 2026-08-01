package com.xuyuchen.health.measurement;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EventTimeAggregationTest {
    @Test
    void windowLastUsesEventTimeInsteadOfArrivalOrder() {
        WindowAggregate aggregate = new WindowAggregate(new WindowKey("p", "d", "c", 0, 60));
        Measurement newer = measurement("2026-08-01T00:00:20Z", 2, 20);
        Measurement older = measurement("2026-08-01T00:00:10Z", 1, 10);

        aggregate.add(newer);
        aggregate.add(older);

        assertEquals(20, aggregate.last());
        assertEquals(newer.eventTime(), aggregate.lastTime());
    }

    @Test
    void windowLastUsesSequenceAsTieBreaker() {
        WindowAggregate aggregate = new WindowAggregate(new WindowKey("p", "d", "c", 0, 60));
        aggregate.add(measurement("2026-08-01T00:00:10Z", 5, 5));
        aggregate.add(measurement("2026-08-01T00:00:10Z", 6, 6));

        assertEquals(6, aggregate.last());
    }

    @Test
    void downsampleLastUsesEventTime() {
        InMemoryMeasurementHistoryStore history = new InMemoryMeasurementHistoryStore();
        history.append(measurement("2026-08-01T00:00:20Z", 2, 20));
        history.append(measurement("2026-08-01T00:00:10Z", 1, 10));

        DownsamplePoint point = new DownsamplingService(history)
                .downsample("p", "d", "c", null, null, 60, 10).get(0);

        assertEquals(20, point.last());
    }

    private static Measurement measurement(String eventTime, long sequence, double value) {
        return new Measurement("p", "d", "c", Instant.parse(eventTime), sequence, value, "mm", "m-" + sequence);
    }
}
