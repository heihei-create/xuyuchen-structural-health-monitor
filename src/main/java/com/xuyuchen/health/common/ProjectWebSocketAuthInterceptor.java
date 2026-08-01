package com.xuyuchen.health.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

@Component
public class ProjectWebSocketAuthInterceptor implements HandshakeInterceptor {
    private final boolean enabled;
    private final String adminToken;
    private final Map<String, String> projectTokens;

    public ProjectWebSocketAuthInterceptor(
            @Value("${health.auth.enabled:true}") boolean enabled,
            @Value("${health.auth.admin-token:${HEALTH_ADMIN_TOKEN:}}") String adminToken,
            @Value("${health.auth.project-tokens:${HEALTH_PROJECT_TOKENS:}}") String configuredTokens) {
        this.enabled = enabled;
        this.adminToken = adminToken;
        this.projectTokens = parse(configuredTokens);
    }

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler handler, Map<String, Object> attributes) {
        if (!enabled) return true;
        String admin = request.getHeaders().getFirst("X-Admin-Token");
        String path = request.getURI().getPath();
        String prefix = "/ws/";
        String projectId = path.startsWith(prefix) ? path.substring(prefix.length()).split("/", 2)[0] : "";
        if (matches(adminToken, admin) && !projectId.isBlank()) { attributes.put("authenticated", true); attributes.put("projectId", projectId); return true; }
        String token = request.getHeaders().getFirst("X-Project-Token");
        if (token == null && request.getURI().getQuery() != null) token = queryToken(request.getURI().getQuery());
        String resolvedToken = token;
        boolean valid = projectTokens.containsKey(projectId) && matches(projectTokens.get(projectId), resolvedToken);
        if (valid) { attributes.put("authenticated", true); attributes.put("projectId", projectId); }
        return valid;
    }

    @Override public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler handler, Exception exception) {}

    private String queryToken(String query) {
        for (String part : query.split("&")) {
            String[] pair = part.split("=", 2);
            if (pair.length == 2 && (pair[0].equals("access_token") || pair[0].equals("projectToken"))) return pair[1];
        }
        return null;
    }
    private boolean matches(String expected, String actual) {
        return expected != null && actual != null && MessageDigest.isEqual(expected.getBytes(StandardCharsets.UTF_8), actual.getBytes(StandardCharsets.UTF_8));
    }
    private Map<String, String> parse(String raw) {
        Map<String, String> result = new HashMap<>();
        if (raw != null) for (String entry : raw.split(",")) {
            String[] pair = entry.trim().split("=", 2);
            if (pair.length == 2 && !pair[0].isBlank() && !pair[1].isBlank()) result.put(pair[0].trim(), pair[1].trim());
        }
        return Map.copyOf(result);
    }
}
