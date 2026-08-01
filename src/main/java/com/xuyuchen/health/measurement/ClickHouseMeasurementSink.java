package com.xuyuchen.health.measurement;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
@ConditionalOnProperty(name = "health.storage", havingValue = "clickhouse")
public class ClickHouseMeasurementSink {
    private final RestClient client;
    private final MeasurementBatchEncoder encoder;
    public ClickHouseMeasurementSink(RestClient.Builder builder, MeasurementBatchEncoder encoder) {
        this.client = builder.baseUrl("http://${CLICKHOUSE_HOST:localhost}:${CLICKHOUSE_PORT:8123}").build();
        this.encoder = encoder;
    }
    public void write(List<Measurement> measurements) {
        if (measurements.isEmpty()) return;
        client.post().uri("/?query=INSERT%20INTO%20measurements%20FORMAT%20JSONEachRow").body(encoder.toClickHouseJsonEachRow(measurements)).retrieve().toBodilessEntity();
    }
}
