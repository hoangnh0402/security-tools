package com.hqc.security.api.dto.request;

import com.hqc.security.common.domain.model.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateUserRequest(
    @NotBlank(message = "Username is required")
    String username,
    
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    String email,
    
    @NotBlank(message = "Password is required")
    String password,
    
    @NotNull(message = "Role is required")
    Role role
) {}
