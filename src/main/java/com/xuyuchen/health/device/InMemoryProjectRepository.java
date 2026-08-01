package com.xuyuchen.health.device;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Repository
public class InMemoryProjectRepository implements ProjectRepository {
    private final ConcurrentMap<String, Project> records = new ConcurrentHashMap<>();
    @Override public Project save(Project project) { records.put(project.getId(), project); return project; }
    @Override public Optional<Project> find(String projectId) { return Optional.ofNullable(records.get(projectId)); }
    @Override public List<Project> list() { return records.values().stream().toList(); }
}
