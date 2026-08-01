package com.xuyuchen.health.measurement;

import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.LongAdder;

@Component
public class MeasurementMetrics {
    private final LongAdder accepted = new LongAdder();
    private final LongAdder duplicates = new LongAdder();
    private final LongAdder outOfOrder = new LongAdder();
    private final LongAdder invalid = new LongAdder();
    private final LongAdder alerts = new LongAdder();
    public void accepted() { accepted.increment(); }
    public void duplicate() { duplicates.increment(); }
    public void outOfOrder() { outOfOrder.increment(); }
    public void invalid() { invalid.increment(); }
    public void alert() { alerts.increment(); }
    public Snapshot snapshot() { return new Snapshot(accepted.sum(), duplicates.sum(), outOfOrder.sum(), invalid.sum(), alerts.sum()); }
    public record Snapshot(long accepted, long duplicates, long outOfOrder, long invalid, long alerts) {}
}
