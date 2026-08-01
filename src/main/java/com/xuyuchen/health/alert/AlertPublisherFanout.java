package com.xuyuchen.health.alert;

import org.springframework.stereotype.Component;
import org.springframework.context.annotation.Primary;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

@Component
@Primary
@ConditionalOnProperty(name = "health.messaging", havingValue = "memory", matchIfMissing = true)
public class AlertPublisherFanout implements AlertEventPublisher {
    private final InMemoryAlertEventPublisher memory;
    private final AlertWebSocketBroadcaster websocket;
    public AlertPublisherFanout(InMemoryAlertEventPublisher memory, AlertWebSocketBroadcaster websocket) { this.memory = memory; this.websocket = websocket; }
    @Override public void publish(AlertEvent event) { memory.publish(event); websocket.broadcast(event); }
}
