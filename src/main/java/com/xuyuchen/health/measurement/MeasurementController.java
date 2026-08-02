package com.xuyuchen.health.measurement;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import com.xuyuchen.health.device.DeviceTokenService;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/v1/projects/{projectId}/measurements")
public class MeasurementController {
    private final MeasurementIngestionService ingestion;
    private final MeasurementQueryService query;
    private final WindowAggregator windows;
    private final DeviceTokenService deviceTokens;
    public MeasurementController(MeasurementIngestionService ingestion, MeasurementQueryService query, WindowAggregator windows, DeviceTokenService deviceTokens) {
        this.ingestion = ingestion; this.query = query; this.windows = windows; this.deviceTokens = deviceTokens;
    }

    public record WindowResponse(
            String projectId,
            String deviceId,
            String channelId,
            long windowStartEpochSecond,
            int seconds,
            int count,
            double average,
            double min,
            double max,
            double last,
            Instant firstTime,
            Instant lastTime
    ) {
        static WindowResponse from(WindowAggregate aggregate) {
            WindowKey key = aggregate.key();
            return new WindowResponse(key.projectId(), key.deviceId(), key.channelId(), key.windowStartEpochSecond(), key.seconds(),
                    aggregate.count(), aggregate.average(), aggregate.min(), aggregate.max(), aggregate.last(),
                    aggregate.firstTime(), aggregate.lastTime());
        }
    }
    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public MeasurementDtos.IngestResponse ingest(@PathVariable String projectId, @RequestHeader("X-Device-Token") String token, @Valid @RequestBody MeasurementDtos.IngestRequest req) {
        if (!deviceTokens.valid(token, projectId, req.deviceId())) throw new IllegalArgumentException("invalid device token");
        return ingestion.ingest(req.toMeasurement(projectId));
    }
    @GetMapping("/devices/{deviceId}/latest")
    public List<Measurement> latest(@PathVariable String projectId, @PathVariable String deviceId) { return query.latest(projectId, deviceId); }
    @GetMapping("/devices/{deviceId}/channels/{channelId}")
    public List<Measurement> query(@PathVariable String projectId, @PathVariable String deviceId, @PathVariable String channelId, @RequestParam(required = false) Instant from, @RequestParam(required = false) Instant to, @RequestParam(defaultValue = "1000") int limit) {
        return query.query(projectId, deviceId, channelId, from, to, limit);
    }
    @GetMapping("/devices/{deviceId}/channels/{channelId}/windows")
    public List<WindowResponse> windows(@PathVariable String projectId, @PathVariable String deviceId, @PathVariable String channelId, @RequestParam(defaultValue = "60") int seconds) {
        return windows.list(projectId, deviceId, channelId, seconds).stream().map(WindowResponse::from).toList();
    }
}
