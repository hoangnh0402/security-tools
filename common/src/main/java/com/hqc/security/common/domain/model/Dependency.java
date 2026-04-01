package com.hqc.security.common.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

public record Dependency(
    UUID id,
    UUID projectId,
    String name,
    String version,
    String ecosystem,
    LocalDateTime createdAt
) {}
