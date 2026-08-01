package com.xuyuchen.health.measurement;

import java.time.Instant;
import java.util.Map;

public record RawSensorMessage(String projectId, String deviceId, String messageId, Instant receivedAt, Map<String, Object> payload) {}
