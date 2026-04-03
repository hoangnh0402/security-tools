package com.hqc.security.api.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

public record ApiSecurityTestRequest(
    @NotNull UUID projectId,
    @NotNull UUID scanJobId,
    @NotEmpty List<String> endpointUrls
) {}
