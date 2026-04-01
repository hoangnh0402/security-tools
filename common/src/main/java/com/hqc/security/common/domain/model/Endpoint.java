package com.hqc.security.common.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

public record Endpoint(
    UUID id,
    UUID projectId,
    String url,
    String method,
    String parameters, // Chứa mảng JSON string cho đơn giản
    LocalDateTime discoveredAt
) {
    public Endpoint {
        if (url == null || url.isBlank()) {
            throw new IllegalArgumentException("Endpoint URL cannot be empty");
        }
        if (method == null || method.isBlank()) {
            throw new IllegalArgumentException("HTTP Method cannot be empty");
        }
    }
}
