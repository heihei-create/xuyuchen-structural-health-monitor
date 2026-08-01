package com.xuyuchen.health.measurement;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public final class MeasurementDtos {
    private MeasurementDtos() {}
    public record IngestRequest(
            @NotBlank String deviceId,
            @NotBlank String channelId,
            @NotNull Instant eventTime,
            long sequence,
            double value,
            @NotBlank String unit,
            @NotBlank String messageId
    ) {
        Measurement toMeasurement(String projectId) {
            return new Measurement(projectId, deviceId, channelId, eventTime, sequence, value, unit, messageId);
        }
    }
    public record IngestResponse(String projectId, String deviceId, String channelId, boolean accepted, boolean duplicate, boolean outOfOrder, String latestValue) {}
}
