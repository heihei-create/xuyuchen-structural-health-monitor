package com.xuyuchen.health.measurement;

import java.time.Instant;

public record DownsamplePoint(Instant bucket, int count, double min, double max, double average, double last) {}
