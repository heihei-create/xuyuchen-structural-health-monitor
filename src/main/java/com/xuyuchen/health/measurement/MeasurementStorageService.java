package com.xuyuchen.health.measurement;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MeasurementStorageService {
    private final MeasurementHistoryStore history;
    private final WindowAggregator windows;
    public MeasurementStorageService(MeasurementHistoryStore history, WindowAggregator windows) { this.history = history; this.windows = windows; }
    public StorageBatchResult persist(List<Measurement> measurements) {
        for (Measurement measurement : measurements) { history.append(measurement); windows.add(measurement, 60); }
        return new StorageBatchResult(measurements.size(), measurements.stream().mapToDouble(Measurement::value).average().orElse(0));
    }
    public record StorageBatchResult(int count, double average) {}
}
