package com.xuyuchen.health.measurement;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestClient;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.time.Duration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;

@Repository
@ConditionalOnProperty(name = "health.storage", havingValue = "clickhouse")
public class ClickHouseMeasurementHistoryStore implements MeasurementHistoryStore {
    private final RestClient client;
    private final ObjectMapper mapper;
    private final MeasurementBatchEncoder encoder;

    public ClickHouseMeasurementHistoryStore(RestClient.Builder builder, ObjectMapper mapper, MeasurementBatchEncoder encoder,
                                             @org.springframework.beans.factory.annotation.Value("${health.clickhouse.host:localhost}") String host,
                                             @org.springframework.beans.factory.annotation.Value("${health.clickhouse.port:8123}") int port,
                                             @org.springframework.beans.factory.annotation.Value("${health.clickhouse.connect-timeout:2s}") Duration connectTimeout,
                                             @org.springframework.beans.factory.annotation.Value("${health.clickhouse.read-timeout:5s}") Duration readTimeout) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(connectTimeout); requestFactory.setReadTimeout(readTimeout);
        this.client = builder.baseUrl("http://" + host + ":" + port).requestFactory(requestFactory).build();
        this.mapper = mapper;
        this.encoder = encoder;
    }

    @Override public void append(Measurement measurement) {
        client.post().uri(uri -> uri.path("/").queryParam("query", "INSERT INTO health.measurements FORMAT JSONEachRow").build())
                .contentType(MediaType.APPLICATION_NDJSON).body(encoder.toClickHouseJsonEachRow(List.of(measurement))).retrieve().toBodilessEntity();
    }

    @Override public List<Measurement> query(String projectId, String deviceId, String channelId, Instant from, Instant to, int limit) {
        StringBuilder query = new StringBuilder("SELECT project_id,device_id,channel_id,event_time,sequence,value,unit,message_id FROM health.measurements WHERE project_id=")
                .append(quote(projectId)).append(" AND device_id=").append(quote(deviceId)).append(" AND channel_id=").append(quote(channelId));
        if (from != null) query.append(" AND event_time >= '").append(from).append("'");
        if (to != null) query.append(" AND event_time <= '").append(to).append("'");
        query.append(" ORDER BY event_time DESC, sequence DESC LIMIT ").append(Math.min(Math.max(1, limit), 10000)).append(" FORMAT JSONEachRow");
        String body = client.get().uri(uri -> uri.path("/").queryParam("query", query).build()).retrieve().body(String.class);
        if (body == null || body.isBlank()) return List.of();
        List<Measurement> result = new ArrayList<>();
        try {
            for (String line : body.split("\\R")) if (!line.isBlank()) {
                JsonNode row = mapper.readTree(line);
                result.add(new Measurement(row.get("project_id").asText(), row.get("device_id").asText(), row.get("channel_id").asText(), parseTime(row.get("event_time").asText()), row.get("sequence").asLong(), row.get("value").asDouble(), row.get("unit").asText(), row.get("message_id").asText()));
            }
            return result;
        } catch (Exception ex) { throw new IllegalStateException("clickhouse measurement query failed", ex); }
    }

    private String quote(String value) { return "'" + value.replace("'", "''") + "'"; }
    private Instant parseTime(String value) { return value.endsWith("Z") ? Instant.parse(value) : LocalDateTime.parse(value.replace(' ', 'T')).toInstant(ZoneOffset.UTC); }
}
