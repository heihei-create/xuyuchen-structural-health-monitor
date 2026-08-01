package com.xuyuchen.health.device;

import org.springframework.stereotype.Service;

@Service
public class ProjectAccessService {
    private final ProjectRepository projects;
    public ProjectAccessService(ProjectRepository projects) { this.projects = projects; }
    public Project require(String projectId) { return projects.find(projectId).orElseThrow(() -> new IllegalArgumentException("project not found")); }
    public void requireMember(String projectId, String userId) {
        Project project = require(projectId);
        if (!project.isEnabled() || (userId != null && !project.getMembers().contains(userId))) throw new IllegalArgumentException("project access denied");
    }
    public boolean canRead(String projectId, String userId) {
        try { requireMember(projectId, userId); return true; } catch (IllegalArgumentException ex) { return false; }
    }
}
