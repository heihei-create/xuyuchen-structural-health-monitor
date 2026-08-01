package com.xuyuchen.health.alert;

public interface AlertEventPublisher {
    void publish(AlertEvent event);
}
