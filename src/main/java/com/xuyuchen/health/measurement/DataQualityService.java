package com.xuyuchen.health.measurement;

import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;

@Service
public class DataQualityService {
    private final Duration maxFutureSkew = Duration.ofMinutes(5);
    private final Duration maxLate = Duration.ofHours(24);
    public DataQualityResult validate(Measurement measurement, Instant receivedAt) {
        if (measurement == null || receivedAt == null) return new DataQualityResult(DataQuality.INVALID, "measurement and received time are required", false);
        if (measurement.projectId() == null || measurement.projectId().isBlank() || measurement.deviceId() == null || measurement.deviceId().isBlank()
                || measurement.channelId() == null || measurement.channelId().isBlank() || measurement.messageId() == null || measurement.messageId().isBlank()
                || measurement.eventTime() == null) return new DataQualityResult(DataQuality.INVALID, "measurement identity and event time are required", false);
        if (!Double.isFinite(measurement.value())) return new DataQualityResult(DataQuality.INVALID, "value is not finite", false);
        if (measurement.eventTime().isAfter(receivedAt.plus(maxFutureSkew))) return new DataQualityResult(DataQuality.INVALID, "event time is too far in future", false);
        if (measurement.eventTime().isBefore(receivedAt.minus(maxLate))) return new DataQualityResult(DataQuality.LATE, "event is outside late window", false);
        if (measurement.sequence() < 0) return new DataQualityResult(DataQuality.INVALID, "sequence must be non-negative", false);
        return new DataQualityResult(DataQuality.GOOD, "accepted", true);
    }
}
