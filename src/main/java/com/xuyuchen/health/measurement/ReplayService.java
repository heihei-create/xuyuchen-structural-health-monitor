package com.xuyuchen.health.measurement;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReplayService {
    private final MeasurementBatchService batch;
    public ReplayService(MeasurementBatchService batch) { this.batch = batch; }
    public MeasurementBatchService.BatchResult replay(List<Measurement> records, double speed) {
        if (speed <= 0) throw new IllegalArgumentException("replay speed must be positive");
        return batch.ingest(records);
    }
}
