package com.xuyuchen.health.device;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public final class ChannelDtos {
    private ChannelDtos() {}
    public record CreateChannelRequest(@NotBlank String channelId, @NotBlank String metric, @NotBlank String unit, double minValue, double maxValue, @Min(1) int sampleIntervalSeconds) {}
}
