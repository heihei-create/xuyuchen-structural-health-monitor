package com.xuyuchen.health.measurement;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class MeasurementPartitionService {
    private final Map<String, MeasurementPartition> partitions = new ConcurrentHashMap<>();
    public MeasurementPartition record(String projectId, Instant eventTime, long rows, long bytes) {
        LocalDate date = eventTime.atZone(ZoneOffset.UTC).toLocalDate();
        String table = "measurements_" + date.toString().replace("-", "");
        String key = projectId + ":" + date;
        MeasurementPartition previous = partitions.get(key);
        MeasurementPartition next = new MeasurementPartition(projectId, date, table, (previous == null ? 0 : previous.rows()) + rows, (previous == null ? 0 : previous.bytes()) + bytes);
        partitions.put(key, next); return next;
    }
    public List<MeasurementPartition> list(String projectId) { return partitions.values().stream().filter(p -> p.projectId().equals(projectId)).toList(); }
}
