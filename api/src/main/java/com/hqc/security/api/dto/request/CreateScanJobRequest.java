package com.hqc.security.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record CreateScanJobRequest(
    @NotNull(message = "Project ID is required")
    UUID projectId,
    
    @NotNull(message = "Initiator ID is required")
    UUID initiatorId,
    
    @NotBlank(message = "Target URL is required")
    String targetUrl,
    
    @NotBlank(message = "Scan type is required")
    String scanType
) {}
