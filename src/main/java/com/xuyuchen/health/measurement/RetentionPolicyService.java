package com.xuyuchen.health.measurement;

import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RetentionPolicyService {
    private final Map<String, RetentionPolicy> policies = new ConcurrentHashMap<>();
    public RetentionPolicy configure(String projectId, RetentionPolicy policy) { policies.put(projectId, policy); return policy; }
    public RetentionPolicy get(String projectId) { return policies.getOrDefault(projectId, new RetentionPolicy(Duration.ofDays(30), Duration.ofDays(365), 60)); }
    public void remove(String projectId) { policies.remove(projectId); }
}
