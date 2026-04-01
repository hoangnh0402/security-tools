package com.hqc.security.common.domain.service;

import com.hqc.security.common.domain.model.Project;
import com.hqc.security.common.domain.port.in.ProjectService;
import com.hqc.security.common.domain.port.out.ProjectRepository;
import com.hqc.security.common.domain.port.out.UserRepository;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Named
public class ProjectServiceImpl implements ProjectService {
    
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    @Inject
    public ProjectServiceImpl(ProjectRepository projectRepository, UserRepository userRepository) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Project createProject(String name, String description, UUID creatorId) {
        userRepository.findById(creatorId)
                .orElseThrow(() -> new IllegalArgumentException("User not found to act as creator"));
                
        Project project = new Project(null, name, description, creatorId, LocalDateTime.now());
        return projectRepository.save(project);
    }

    @Override
    public Project getProjectById(UUID id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Project not found with ID: " + id));
    }

    @Override
    public List<Project> getProjectsByUser(UUID userId) {
        return projectRepository.findAllByCreatedBy(userId);
    }

    @Override
    public void deleteProject(UUID id) {
        projectRepository.deleteById(id);
    }
}
