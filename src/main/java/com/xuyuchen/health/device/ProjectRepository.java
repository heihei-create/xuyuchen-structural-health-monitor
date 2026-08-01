package com.xuyuchen.health.device;

import java.util.List;
import java.util.Optional;

public interface ProjectRepository {
    Project save(Project project);
    Optional<Project> find(String projectId);
    List<Project> list();
}
