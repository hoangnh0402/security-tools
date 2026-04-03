package com.hqc.security.common.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

public record PayloadLog(
    UUID id,
    UUID vulnerabilityId,
    String payload,
    String requestData,
    String responseData,
    int responseTime,
    LocalDateTime createdAt
) {}
