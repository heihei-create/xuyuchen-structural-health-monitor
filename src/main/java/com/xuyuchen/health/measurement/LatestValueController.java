package com.xuyuchen.health.measurement;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/projects/{projectId}/latest")
public class LatestValueController {
    private final LatestValueService service;
    public LatestValueController(LatestValueService service) { this.service = service; }
    @GetMapping("/{deviceId}")
    public List<LatestValueProjection> list(@PathVariable String projectId, @PathVariable String deviceId) { return service.list(projectId, deviceId); }
    @GetMapping("/{deviceId}/{channelId}")
    public LatestValueProjection get(@PathVariable String projectId, @PathVariable String deviceId, @PathVariable String channelId) { return service.get(projectId, deviceId, channelId); }
}
