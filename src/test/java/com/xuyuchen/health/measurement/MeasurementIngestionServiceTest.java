package com.xuyuchen.health.measurement;

import com.xuyuchen.health.alert.AlertService;
import com.xuyuchen.health.device.Device;
import com.xuyuchen.health.device.DeviceService;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class MeasurementIngestionServiceTest {
    @Test
    void lateMeasurementIsStoredButDoesNotUpdateDerivedRealtimeState() {
        DeviceService devices = mock(DeviceService.class);
        MessageDeduplicationService dedup = mock(MessageDeduplicationService.class);
        LatestMeasurementStore latest = mock(LatestMeasurementStore.class);
        MeasurementStorageService storage = mock(MeasurementStorageService.class);
        MeasurementEventPublisher publisher = mock(MeasurementEventPublisher.class);
        AlertService alerts = mock(AlertService.class);
        MeasurementMetrics metrics = new MeasurementMetrics();
        Measurement measurement = new Measurement("p", "d", "c", Instant.now(), 1, 10, "mm", "m1");

        when(devices.get("p", "d")).thenReturn(new Device("p", "d", "device", "model", "1.0"));
        when(dedup.reserve("p", "m1")).thenReturn(new MessageDeduplicationService.Reservation("p:m1", "token"));
        when(latest.putIfNewer(measurement)).thenReturn(false);
        when(latest.get("p", "d", "c")).thenReturn(Optional.of(measurement));

        MeasurementDtos.IngestResponse response = new MeasurementIngestionService(
                devices, dedup, latest, storage, publisher, alerts, metrics, new DataQualityService()).ingest(measurement);

        assertTrue(response.accepted());
        assertTrue(response.outOfOrder());
        verify(storage).persist(measurement, false);
        verify(devices, never()).seen(anyString(), anyString(), any());
        verify(publisher, never()).publish(any());
        verifyNoInteractions(alerts);
        assertTrue(metrics.snapshot().outOfOrder() == 1);
    }
}
