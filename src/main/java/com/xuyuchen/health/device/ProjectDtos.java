package com.xuyuchen.health.device;

import jakarta.validation.constraints.NotBlank;

import java.util.List;

public final class ProjectDtos {
    private ProjectDtos() {}
    public record CreateProjectRequest(@NotBlank String id, @NotBlank String name, @NotBlank String owner) {}
    public record AddMemberRequest(@NotBlank String userId) {}
    public record CreateDeviceRequest(@NotBlank String deviceId, @NotBlank String name, @NotBlank String model, String firmware, List<String> channels) {}
}
