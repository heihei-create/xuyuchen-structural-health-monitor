package com.xuyuchen.health.measurement;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MeasurementStorageService {
    private final MeasurementHistoryStore history;
    private final WindowAggregator windows;
    private final ObjectProvider<ClickHouseMeasurementSink> clickHouse;

    public MeasurementStorageService(MeasurementHistoryStore history, WindowAggregator windows, ObjectProvider<ClickHouseMeasurementSink> clickHouse) {
        this.history = history;
        this.windows = windows;
        this.clickHouse = clickHouse;
    }

    public StorageBatchResult persist(Measurement measurement, boolean updateEventTimeDerivedData) {
        history.append(measurement);
        if (!(history instanceof ClickHouseMeasurementHistoryStore)) clickHouse.ifAvailable(sink -> sink.write(List.of(measurement)));
        if (updateEventTimeDerivedData) {
            windows.add(measurement, 1);
            windows.add(measurement, 60);
        }
        return new StorageBatchResult(1, measurement.value());
    }

    public StorageBatchResult persist(List<Measurement> measurements) {
        for (Measurement measurement : measurements) persist(measurement, true);
        return new StorageBatchResult(measurements.size(), measurements.stream().mapToDouble(Measurement::value).average().orElse(0));
    }
    public record StorageBatchResult(int count, double average) {}
}
