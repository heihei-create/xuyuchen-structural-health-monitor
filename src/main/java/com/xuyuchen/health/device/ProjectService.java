package com.xuyuchen.health.device;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectService {
    private final ProjectRepository projects;
    public ProjectService(ProjectRepository projects) { this.projects = projects; }
    public Project create(String id, String name, String owner) {
        if (projects.find(id).isPresent()) throw new IllegalStateException("project already exists");
        return projects.save(new Project(id, name, owner));
    }
    public Project get(String id) { return projects.find(id).orElseThrow(() -> new IllegalArgumentException("project not found")); }
    public List<Project> list() { return projects.list(); }
    public Project addMember(String id, String user) { Project project = get(id); project.addMember(user); return projects.save(project); }
    public Project disable(String id) { Project project = get(id); project.disable(); return projects.save(project); }
}
