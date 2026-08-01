package com.xuyuchen.health.common;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
public class TraceFilter extends OncePerRequestFilter {
    private static final ThreadLocal<String> TRACE = new ThreadLocal<>();
    public static String id() { return TRACE.get() == null ? "unknown" : TRACE.get(); }
    @Override protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        String trace = request.getHeader("X-Trace-Id");
        TRACE.set(trace == null || trace.isBlank() ? UUID.randomUUID().toString() : trace);
        response.setHeader("X-Trace-Id", id());
        try { chain.doFilter(request, response); } finally { TRACE.remove(); ProjectContext.clear(); }
    }
}
