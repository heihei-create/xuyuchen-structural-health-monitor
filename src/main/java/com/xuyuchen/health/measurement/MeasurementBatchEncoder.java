package com.xuyuchen.health.measurement;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.time.ZoneOffset;
import java.util.LinkedHashMap;
import java.util.List;

@Component
public class MeasurementBatchEncoder {
    private final ObjectMapper mapper;
    public MeasurementBatchEncoder(ObjectMapper mapper) { this.mapper = mapper; }
    public String toClickHouseJsonEachRow(List<Measurement> measurements) {
        StringBuilder output = new StringBuilder();
        for (Measurement m : measurements) {
            try {
                LinkedHashMap<String, Object> row = new LinkedHashMap<>();
                row.put("project_id", m.projectId());
                row.put("device_id", m.deviceId());
                row.put("channel_id", m.channelId());
                row.put("event_time", m.eventTime().atOffset(ZoneOffset.UTC).toString());
                row.put("sequence", m.sequence());
                row.put("value", m.value());
                row.put("unit", m.unit());
                row.put("message_id", m.messageId());
                output.append(mapper.writeValueAsString(row)).append('\n');
            } catch (Exception ex) {
                throw new IllegalStateException("measurement encoding failed", ex);
            }
        }
        return output.toString();
    }
}
