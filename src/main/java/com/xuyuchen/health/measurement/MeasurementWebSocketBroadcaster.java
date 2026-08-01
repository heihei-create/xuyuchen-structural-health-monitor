package com.xuyuchen.health.measurement;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class MeasurementWebSocketBroadcaster {
    private final SimpMessagingTemplate template;
    public MeasurementWebSocketBroadcaster(SimpMessagingTemplate template) { this.template = template; }
    public void broadcast(Measurement measurement) {
        template.convertAndSend("/topic/projects/" + measurement.projectId() + "/measurements", measurement);
    }
}
