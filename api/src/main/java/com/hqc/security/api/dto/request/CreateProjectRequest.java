package com.hqc.security.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record CreateProjectRequest(
    @NotBlank(message = "Project name is required")
    String name,
    
    String description,
    
    @NotNull(message = "Creator ID is required")
    UUID createdBy
) {}
