package com.xuyuchen.health.alert;

public final class AlertFingerprint {
    private AlertFingerprint() {}
    public static String of(String projectId, String deviceId, String channelId, String ruleId, int version) {
        return String.join(":", projectId, deviceId, channelId, ruleId, String.valueOf(version));
    }
}
