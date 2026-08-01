package com.xuyuchen.health.alert;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class AlertWebSocketBroadcaster {
    private final SimpMessagingTemplate template;
    public AlertWebSocketBroadcaster(SimpMessagingTemplate template) { this.template = template; }
    public void broadcast(AlertEvent event) {
        template.convertAndSend("/topic/projects/" + event.projectId() + "/alerts", event);
    }
}
