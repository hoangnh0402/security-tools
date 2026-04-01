package com.hqc.security.api.dto.request;

import jakarta.validation.constraints.NotBlank;

public record DependencyRequest(
    @NotBlank String name,
    String version,
    String ecosystem
) {}
