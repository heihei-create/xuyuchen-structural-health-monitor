package com.xuyuchen.health.measurement;

import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;

@Service
public class DataQualityService {
    private final Duration maxFutureSkew = Duration.ofMinutes(5);
    private final Duration maxLate = Duration.ofHours(24);
    public DataQualityResult validate(Measurement measurement, Instant receivedAt) {
        if (!Double.isFinite(measurement.value())) return new DataQualityResult(DataQuality.INVALID, "value is not finite", false);
        if (measurement.eventTime().isAfter(receivedAt.plus(maxFutureSkew))) return new DataQualityResult(DataQuality.INVALID, "event time is too far in future", false);
        if (measurement.eventTime().isBefore(receivedAt.minus(maxLate))) return new DataQualityResult(DataQuality.LATE, "event is outside late window", false);
        if (measurement.sequence() < 0) return new DataQualityResult(DataQuality.INVALID, "sequence must be non-negative", false);
        return new DataQualityResult(DataQuality.GOOD, "accepted", true);
    }
}
