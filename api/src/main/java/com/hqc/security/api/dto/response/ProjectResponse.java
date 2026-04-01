package com.hqc.security.api.dto.response;

import com.hqc.security.common.domain.model.Project;
import java.time.LocalDateTime;
import java.util.UUID;

public record ProjectResponse(
    UUID id,
    String name,
    String description,
    UUID createdBy,
    LocalDateTime createdAt
) {
    public static ProjectResponse fromDomain(Project project) {
        return new ProjectResponse(
            project.id(),
            project.name(),
            project.description(),
            project.createdBy(),
            project.createdAt()
        );
    }
}
