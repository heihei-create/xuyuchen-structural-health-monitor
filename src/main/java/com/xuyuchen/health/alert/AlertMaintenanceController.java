package com.xuyuchen.health.alert;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/internal/projects/{projectId}/alerts")
public class AlertMaintenanceController {
    private final AlertMaintenanceService service;
    private final NotificationDeliveryService delivery;
    public AlertMaintenanceController(AlertMaintenanceService service, NotificationDeliveryService delivery) { this.service = service; this.delivery = delivery; }
    @GetMapping("/summary")
    public AlertMaintenanceService.MaintenanceSummary summary(@PathVariable String projectId) { return service.summary(projectId); }
    @PostMapping("/deliver")
    public int deliver(@RequestParam(defaultValue = "100") int limit) { return delivery.process(limit); }
}
