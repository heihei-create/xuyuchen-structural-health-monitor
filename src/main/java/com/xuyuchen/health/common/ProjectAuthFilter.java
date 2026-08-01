package com.xuyuchen.health.common;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

@Component
public class ProjectAuthFilter extends OncePerRequestFilter {
    private final boolean enabled;
    private final String adminToken;
    private final Map<String, String> projectTokens;

    public ProjectAuthFilter(
            @Value("${health.auth.enabled:true}") boolean enabled,
            @Value("${health.auth.admin-token:${HEALTH_ADMIN_TOKEN:}}") String adminToken,
            @Value("${health.auth.project-tokens:${HEALTH_PROJECT_TOKENS:}}") String configuredTokens) {
        this.enabled = enabled;
        this.adminToken = adminToken;
        this.projectTokens = parse(configuredTokens);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/actuator/") || path.equals("/error") || path.equals("/ws") || path.startsWith("/ws/") || "OPTIONS".equalsIgnoreCase(request.getMethod());
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        if (!enabled || authorized(request)) {
            chain.doFilter(request, response);
            return;
        }
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "project credentials are required");
    }

    private boolean authorized(HttpServletRequest request) {
        String admin = request.getHeader("X-Admin-Token");
        if (matches(adminToken, admin)) return true;
        String prefix = request.getContextPath() + "/api/v1/projects/";
        String path = request.getRequestURI();
        if (!path.startsWith(prefix)) return false;
        String remainder = path.substring(prefix.length());
        String projectId = remainder.split("/", 2)[0];
        return matches(projectTokens.get(projectId), request.getHeader("X-Project-Token")) && dataPlaneRequest(request, projectId);
    }

    private boolean dataPlaneRequest(HttpServletRequest request, String projectId) {
        String path = request.getRequestURI();
        String projectPrefix = request.getContextPath() + "/api/v1/projects/" + projectId;
        if (path.startsWith(projectPrefix + "/measurements") || path.startsWith(projectPrefix + "/ingestion")) return "GET".equalsIgnoreCase(request.getMethod()) || "POST".equalsIgnoreCase(request.getMethod());
        if (path.startsWith(projectPrefix + "/latest") || path.startsWith(projectPrefix + "/alerts")) return "GET".equalsIgnoreCase(request.getMethod());
        return false;
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
