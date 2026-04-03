package com.hqc.security.common.domain.model;

import java.util.UUID;

public record ScanTarget(
    UUID id,
    UUID scanJobId,
    String targetUrl,
    String method,
    String headers,   // JSON string
    String body
) {
    public ScanTarget {
        if (scanJobId == null) {
            throw new IllegalArgumentException("ScanTarget must belong to a ScanJob");
        }
        if (targetUrl == null || targetUrl.isBlank()) {
            throw new IllegalArgumentException("Target URL cannot be empty");
        }
    }
}
