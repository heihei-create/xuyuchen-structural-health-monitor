package com.xuyuchen.health.measurement;

import org.springframework.stereotype.Service;

import java.util.ArrayDeque;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class IngestionQueue {
    private final Map<String, Queue<Measurement>> queues = new ConcurrentHashMap<>();
    private final Map<String, BackpressurePolicy> policies = new ConcurrentHashMap<>();
    public void configure(String projectId, BackpressurePolicy policy) { policies.put(projectId, policy); }
    public synchronized boolean offer(Measurement measurement) {
        String projectId = measurement.projectId();
        Queue<Measurement> queue = queues.computeIfAbsent(projectId, id -> new ArrayDeque<>());
        BackpressurePolicy policy = policies.getOrDefault(projectId, BackpressurePolicy.defaultPolicy());
        if (queue.size() >= policy.queueLimit()) {
            if (!policy.dropOldest()) return false;
            queue.poll();
        }
        queue.offer(measurement); return true;
    }
    public synchronized Measurement poll(String projectId) {
        Queue<Measurement> queue = queues.get(projectId);
        return queue == null ? null : queue.poll();
    }
    public synchronized int size(String projectId) { Queue<Measurement> queue = queues.get(projectId); return queue == null ? 0 : queue.size(); }
}
