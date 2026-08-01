package com.xuyuchen.health.measurement;

import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestController
@RequestMapping("/api/v1/projects/{projectId}/trends")
public class TrendController {
    private final TrendQueryService service;
    public TrendController(TrendQueryService service) { this.service = service; }
    @GetMapping("/{deviceId}/{channelId}")
    public TrendQueryService.TrendResult query(@PathVariable String projectId, @PathVariable String deviceId, @PathVariable String channelId, @RequestParam Instant from, @RequestParam Instant to, @RequestParam(defaultValue = "60") int bucketSeconds, @RequestParam(defaultValue = "500") int limit) {
        return service.query(projectId, deviceId, channelId, from, to, bucketSeconds, limit);
    }
}
