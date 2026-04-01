package com.hqc.security.api.dto.response;

import com.hqc.security.common.domain.model.Role;
import com.hqc.security.common.domain.model.User;
import java.time.LocalDateTime;
import java.util.UUID;

public record UserResponse(
    UUID id,
    String username,
    String email,
    Role role,
    LocalDateTime createdAt
) {
    public static UserResponse fromDomain(User user) {
        return new UserResponse(
            user.id(),
            user.username(),
            user.email(),
            user.role(),
            user.createdAt()
        );
    }
}
