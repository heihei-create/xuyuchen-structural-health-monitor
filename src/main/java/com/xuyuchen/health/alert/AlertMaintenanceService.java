package com.xuyuchen.health.alert;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Service
public class AlertMaintenanceService {
    private final AlertRepository alerts;
    public AlertMaintenanceService(AlertRepository alerts) { this.alerts = alerts; }
    public MaintenanceSummary summary(String projectId) {
        List<AlertEvent> records = alerts.findByProject(projectId);
        long active = records.stream().filter(e -> e.status() == AlertStatus.TRIGGERED || e.status() == AlertStatus.ACKED).count();
        long recovered = records.stream().filter(e -> e.status() == AlertStatus.RECOVERED).count();
        return new MaintenanceSummary(projectId, records.size(), active, recovered, Instant.now(), Map.of("triggered", active, "recovered", recovered));
    }
    public record MaintenanceSummary(String projectId, long total, long active, long recovered, Instant generatedAt, Map<String, Long> byStatus) {}
}
