package com.xuyuchen.health.alert;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public final class AlertRuleDtos {
    private AlertRuleDtos() {}
    public record CreateRuleRequest(@NotBlank String ruleId, @NotBlank String channelId, double upperLimit, double lowerLimit, @Min(1) int consecutiveSamples, @Min(1) int recoverySamples, @Min(1) int version) {}
}
