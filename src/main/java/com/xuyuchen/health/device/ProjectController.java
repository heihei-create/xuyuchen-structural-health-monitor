package com.xuyuchen.health.device;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.xuyuchen.health.device.ProjectDtos.*;

@RestController
@RequestMapping("/api/v1/projects")
public class ProjectController {
    private final ProjectService service;
    public ProjectController(ProjectService service) { this.service = service; }
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Project create(@Valid @RequestBody CreateProjectRequest req) { return service.create(req.id(), req.name(), req.owner()); }
    @GetMapping
    public List<Project> list() { return service.list(); }
    @PostMapping("/{projectId}/members")
    public Project addMember(@PathVariable String projectId, @Valid @RequestBody AddMemberRequest req) { return service.addMember(projectId, req.userId()); }
    @PostMapping("/{projectId}/disable")
    public Project disable(@PathVariable String projectId) { return service.disable(projectId); }
}
