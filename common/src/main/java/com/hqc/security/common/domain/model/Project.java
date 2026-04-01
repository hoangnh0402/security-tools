package com.hqc.security.common.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

public record Project(
    UUID id,
    String name,
    String description,
    UUID createdBy,
    LocalDateTime createdAt
) {
    public Project {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Project name must not be empty");
        }
        if (createdBy == null) {
            throw new IllegalArgumentException("Project must have an owner/creator");
        }
    }
}
