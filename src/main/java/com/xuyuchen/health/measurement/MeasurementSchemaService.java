package com.xuyuchen.health.measurement;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class MeasurementSchemaService {
    private final Map<String, Schema> schemas = new ConcurrentHashMap<>();
    public Schema configure(String projectId, int version, Map<String, String> fields) {
        if (version < 1 || fields == null || fields.isEmpty()) throw new IllegalArgumentException("schema must contain fields");
        Schema schema = new Schema(projectId, version, Map.copyOf(fields)); schemas.put(projectId + ":" + version, schema); return schema;
    }
    public Schema get(String projectId, int version) { return schemas.getOrDefault(projectId + ":" + version, new Schema(projectId, version, Map.of())); }
    public boolean supports(String projectId, int version, String field) { return get(projectId, version).fields().containsKey(field); }
    public record Schema(String projectId, int version, Map<String, String> fields) {}
}
