package com.xuyuchen.health.alert;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@ConditionalOnProperty(name = "health.messaging", havingValue = "kafka")
public class KafkaAlertEventPublisher implements AlertEventPublisher {
    private final KafkaTemplate<String, AlertEvent> kafka;

    public KafkaAlertEventPublisher(KafkaTemplate<String, AlertEvent> kafka) { this.kafka = kafka; }

    @Override
    public void publish(AlertEvent event) {
        try {
            kafka.send("alerts.events", event.projectId() + ":" + event.fingerprint(), event).get(5, TimeUnit.SECONDS);
        } catch (Exception ex) {
            throw new IllegalStateException("alert event publish failed", ex);
        }
    }
}
