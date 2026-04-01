package com.hqc.security.common.domain.port.in;

import com.hqc.security.common.domain.model.Project;
import java.util.List;
import java.util.UUID;

public interface ProjectService {
    Project createProject(String name, String description, UUID creatorId);
    Project getProjectById(UUID id);
    List<Project> getProjectsByUser(UUID userId);
    void deleteProject(UUID id);
}
