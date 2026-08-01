package com.xuyuchen.health.measurement;

import java.time.Instant;

public class WindowAggregate {
    private final WindowKey key;
    private int count;
    private double sum;
    private double min = Double.POSITIVE_INFINITY;
    private double max = Double.NEGATIVE_INFINITY;
    private double last;
    private long lastSequence;
    private Instant firstTime;
    private Instant lastTime;
    public WindowAggregate(WindowKey key) { this.key = key; }
    public synchronized void add(Measurement measurement) {
        count++; sum += measurement.value(); min = Math.min(min, measurement.value()); max = Math.max(max, measurement.value());
        if (firstTime == null || measurement.eventTime().isBefore(firstTime)) firstTime = measurement.eventTime();
        if (lastTime == null || measurement.eventTime().isAfter(lastTime)
                || (measurement.eventTime().equals(lastTime) && measurement.sequence() > lastSequence)) {
            last = measurement.value();
            lastSequence = measurement.sequence();
            lastTime = measurement.eventTime();
        }
    }
    public WindowKey key() { return key; }
    public synchronized int count() { return count; }
    public synchronized double average() { return count == 0 ? 0 : sum / count; }
    public synchronized double min() { return count == 0 ? 0 : min; }
    public synchronized double max() { return count == 0 ? 0 : max; }
    public synchronized double last() { return last; }
    public synchronized Instant firstTime() { return firstTime; }
    public synchronized Instant lastTime() { return lastTime; }
}
