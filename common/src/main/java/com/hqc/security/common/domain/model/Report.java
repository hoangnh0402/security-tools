package com.hqc.security.common.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

public record Report(
    UUID id,
    UUID projectId,
    UUID scanJobId,
    String summary,    // JSON string
    String filePath,
    LocalDateTime createdAt
) {
    public Report {
        if (projectId == null) {
            throw new IllegalArgumentException("Report must belong to a project");
        }
    }
}
