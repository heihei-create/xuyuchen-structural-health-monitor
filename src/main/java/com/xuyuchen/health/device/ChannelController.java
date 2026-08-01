package com.xuyuchen.health.device;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.xuyuchen.health.device.ChannelDtos.*;

@RestController
@RequestMapping("/api/v1/projects/{projectId}/devices/{deviceId}/channels")
public class ChannelController {
    private final DeviceChannelService service;
    public ChannelController(DeviceChannelService service) { this.service = service; }
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DeviceChannel create(@PathVariable String projectId, @PathVariable String deviceId, @Valid @RequestBody CreateChannelRequest req) {
        return service.register(projectId, deviceId, req.channelId(), req.metric(), req.unit(), req.minValue(), req.maxValue(), req.sampleIntervalSeconds());
    }
    @GetMapping
    public List<DeviceChannel> list(@PathVariable String projectId, @PathVariable String deviceId) { return service.list(projectId, deviceId); }
}
