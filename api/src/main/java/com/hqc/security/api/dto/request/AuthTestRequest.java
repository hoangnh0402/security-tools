package com.hqc.security.api.dto.request;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record AuthTestRequest(
    @NotNull UUID projectId,
    @NotNull UUID scanJobId,
    String loginUrl,
    String jwtToken,
    String targetUrl
) {}
