package com.xuyuchen.health.common;

public final class ProjectContext {
    private static final ThreadLocal<String> CURRENT = new ThreadLocal<>();
    private ProjectContext() {}
    public static void set(String id) { CURRENT.set(id); }
    public static String require() {
        String id = CURRENT.get();
        if (id == null || id.isBlank()) throw new IllegalArgumentException("project id is required");
        return id;
    }
    public static void clear() { CURRENT.remove(); }
}
