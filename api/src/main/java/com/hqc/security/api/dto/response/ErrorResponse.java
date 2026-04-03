package com.hqc.security.api.dto.response;

import java.time.LocalDateTime;

/**
 * Cấu trúc JSON lỗi thống nhất cho toàn bộ REST API.
 * Theo chuẩn HQC Spring Boot Coding Standards.
 */
public record ErrorResponse(
    LocalDateTime timestamp,
    int status,
    String error,
    String message,
    String path
) {}
