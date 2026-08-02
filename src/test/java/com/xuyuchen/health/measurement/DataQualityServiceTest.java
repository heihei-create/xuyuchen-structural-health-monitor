package com.xuyuchen.health.measurement;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;

class DataQualityServiceTest {
    @Test
    void malformedIdentityIsRejectedAsDataQualityFailure() {
        Measurement measurement = new Measurement("", "device", "channel", Instant.parse("2026-08-01T00:00:00Z"), 1, 1.0, "mm", "message");

        DataQualityResult result = new DataQualityService().validate(measurement, Instant.parse("2026-08-01T00:00:01Z"));

        assertFalse(result.accepted());
        assertEquals(DataQuality.INVALID, result.quality());
    }
}
