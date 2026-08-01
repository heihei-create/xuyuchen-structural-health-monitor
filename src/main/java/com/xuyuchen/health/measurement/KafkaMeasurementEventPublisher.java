package com.xuyuchen.health.measurement;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "health.messaging", havingValue = "kafka")
public class KafkaMeasurementEventPublisher implements MeasurementEventPublisher {
    private final KafkaTemplate<String, Measurement> kafka;
    public KafkaMeasurementEventPublisher(KafkaTemplate<String, Measurement> kafka) { this.kafka = kafka; }
    @Override public void publish(Measurement measurement) {
        kafka.send("measurements.raw", measurement.projectId() + ":" + measurement.deviceId(), measurement);
    }
}
