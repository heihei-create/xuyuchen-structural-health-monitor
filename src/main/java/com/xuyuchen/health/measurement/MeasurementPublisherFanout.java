package com.xuyuchen.health.measurement;

import org.springframework.stereotype.Component;
import org.springframework.context.annotation.Primary;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

@Component
@Primary
@ConditionalOnProperty(name = "health.messaging", havingValue = "memory", matchIfMissing = true)
public class MeasurementPublisherFanout implements MeasurementEventPublisher {
    private final InMemoryMeasurementEventPublisher memory;
    private final MeasurementWebSocketBroadcaster websocket;
    public MeasurementPublisherFanout(InMemoryMeasurementEventPublisher memory, MeasurementWebSocketBroadcaster websocket) { this.memory = memory; this.websocket = websocket; }
    @Override public void publish(Measurement measurement) { memory.publish(measurement); websocket.broadcast(measurement); }
}
