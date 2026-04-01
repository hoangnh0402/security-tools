package com.hqc.security.common.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

public record User(
    UUID id,
    String username,
    String email,
    String passwordHash,
    Role role,
    LocalDateTime createdAt
) {
    // Validation tự nhiên có thể nhúng tại constructor của record
    public User {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username must not be empty");
        }
        if (email == null || email.isBlank() || !email.contains("@")) {
            throw new IllegalArgumentException("Invalid email format");
        }
    }
}
