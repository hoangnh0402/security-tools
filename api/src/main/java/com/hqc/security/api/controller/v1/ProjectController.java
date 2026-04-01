package com.hqc.security.api.controller.v1;

import com.hqc.security.api.dto.request.CreateProjectRequest;
import com.hqc.security.api.dto.response.ProjectResponse;
import com.hqc.security.common.domain.model.Project;
import com.hqc.security.common.domain.port.in.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProjectResponse createProject(@RequestBody @Valid CreateProjectRequest request) {
        Project project = projectService.createProject(
                request.name(),
                request.description(),
                request.createdBy()
        );
        return ProjectResponse.fromDomain(project);
    }

    @GetMapping("/{id}")
    public ProjectResponse getProject(@PathVariable UUID id) {
        Project project = projectService.getProjectById(id);
        return ProjectResponse.fromDomain(project);
    }
    
    @GetMapping("/user/{userId}")
    public List<ProjectResponse> getProjectsByUser(@PathVariable UUID userId) {
        return projectService.getProjectsByUser(userId).stream()
                .map(ProjectResponse::fromDomain)
                .collect(Collectors.toList());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProject(@PathVariable UUID id) {
        projectService.deleteProject(id);
    }
}
