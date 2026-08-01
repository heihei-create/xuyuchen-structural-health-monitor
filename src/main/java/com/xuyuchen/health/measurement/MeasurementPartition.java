package com.xuyuchen.health.measurement;

import java.time.LocalDate;

public record MeasurementPartition(String projectId, LocalDate date, String tableName, long rows, long bytes) {}
