package com.xuyuchen.health.measurement;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Component
public class SensorMessageDecoder {
    private final ObjectMapper mapper;
    public SensorMessageDecoder(ObjectMapper mapper) { this.mapper = mapper; }
    public List<Measurement> decode(RawSensorMessage raw) {
        Object values = raw.payload().get("values");
        if (!(values instanceof List<?> list)) throw new IllegalArgumentException("values array is required");
        return list.stream().map(value -> decodeOne(raw, value)).toList();
    }
    private Measurement decodeOne(RawSensorMessage raw, Object value) {
        if (!(value instanceof Map<?, ?> map)) throw new IllegalArgumentException("measurement item must be object");
        String channelId = String.valueOf(map.get("channelId"));
        double number = Double.parseDouble(String.valueOf(map.get("value")));
        Instant eventTime = map.get("eventTime") == null ? raw.receivedAt() : Instant.parse(String.valueOf(map.get("eventTime")));
        long sequence = map.get("sequence") == null ? 0 : Long.parseLong(String.valueOf(map.get("sequence")));
        Object unitValue = map.containsKey("unit") ? map.get("unit") : "";
        String unit = String.valueOf(unitValue);
        String id = raw.messageId() + ":" + channelId + ":" + sequence;
        return new Measurement(raw.projectId(), raw.deviceId(), channelId, eventTime, sequence, number, unit, id);
    }
}
