package com.xuyuchen.health.measurement;

public interface MeasurementEventPublisher {
    void publish(Measurement measurement);
}
