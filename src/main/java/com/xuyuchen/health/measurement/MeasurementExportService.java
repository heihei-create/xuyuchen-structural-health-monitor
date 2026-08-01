package com.xuyuchen.health.measurement;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class MeasurementExportService {
    private final MeasurementHistoryStore history;
    private final MeasurementBatchEncoder encoder;
    public MeasurementExportService(MeasurementHistoryStore history, MeasurementBatchEncoder encoder) { this.history = history; this.encoder = encoder; }
    public ExportedBatch export(String projectId, String deviceId, String channelId, Instant from, Instant to, int limit) {
        List<Measurement> records = history.query(projectId, deviceId, channelId, from, to, limit);
        return new ExportedBatch(projectId, deviceId, channelId, records.size(), encoder.toClickHouseJsonEachRow(records));
    }
    public record ExportedBatch(String projectId, String deviceId, String channelId, int count, String jsonEachRow) {}
}
