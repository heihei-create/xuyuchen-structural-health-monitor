package com.xuyuchen.health.measurement;

public record DataQualityResult(DataQuality quality, String reason, boolean accepted) {}
