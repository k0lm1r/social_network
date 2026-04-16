package com.kolmir.identity_service.dto.user;

import com.kolmir.identity_service.model.UserRole;

public record UserResponse(
    Long id,
    String email,
    String username,
    String displayName,
    String bio,
    UserRole role,
    Boolean isEnabled
) {
}
