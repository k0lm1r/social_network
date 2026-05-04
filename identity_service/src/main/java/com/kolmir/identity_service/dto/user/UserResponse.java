package com.kolmir.identity_service.dto.user;

import com.kolmir.auth.model.UserRole;


public record UserResponse(
    Long id,
    String email,
    String username,
    String displayName,
    String bio,
    UserRole role,
    Boolean isEnabled
) {}
