package com.xuyuchen.health.measurement;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.Duration;
import java.util.List;

@Component
@ConditionalOnProperty(name = "health.storage", havingValue = "clickhouse")
public class ClickHouseMeasurementSink {
    private final RestClient client;
    private final MeasurementBatchEncoder encoder;
    public ClickHouseMeasurementSink(
            RestClient.Builder builder,
            MeasurementBatchEncoder encoder,
            @Value("${health.clickhouse.host:localhost}") String host,
            @Value("${health.clickhouse.port:8123}") int port,
            @Value("${health.clickhouse.connect-timeout:2s}") Duration connectTimeout,
            @Value("${health.clickhouse.read-timeout:5s}") Duration readTimeout) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(connectTimeout);
        requestFactory.setReadTimeout(readTimeout);
        this.client = builder.baseUrl("http://" + host + ":" + port).requestFactory(requestFactory).build();
        this.encoder = encoder;
    }
    public void write(List<Measurement> measurements) {
        if (measurements.isEmpty()) return;
        client.post()
                .uri(uriBuilder -> uriBuilder.path("/").queryParam("query", "INSERT INTO health.measurements FORMAT JSONEachRow").build())
                .contentType(MediaType.APPLICATION_NDJSON)
                .body(encoder.toClickHouseJsonEachRow(measurements))
                .retrieve()
                .toBodilessEntity();
    }
}
