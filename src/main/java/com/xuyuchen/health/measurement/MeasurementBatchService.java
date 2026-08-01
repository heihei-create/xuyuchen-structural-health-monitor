package com.xuyuchen.health.measurement;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MeasurementBatchService {
    private final MeasurementIngestionService ingestion;
    public MeasurementBatchService(MeasurementIngestionService ingestion) { this.ingestion = ingestion; }
    public BatchResult ingest(List<Measurement> measurements) {
        int accepted = 0, duplicates = 0, rejected = 0;
        for (Measurement measurement : measurements) {
            try {
                MeasurementDtos.IngestResponse result = ingestion.ingest(measurement);
                if (result.duplicate()) duplicates++; else if (result.accepted()) accepted++; else rejected++;
            } catch (IllegalArgumentException ex) { rejected++; }
        }
        return new BatchResult(measurements.size(), accepted, duplicates, rejected);
    }
    public record BatchResult(int total, int accepted, int duplicates, int rejected) {}
}
