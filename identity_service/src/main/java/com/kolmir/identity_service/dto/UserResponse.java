package com.kolmir.identity_service.dto;


public record UserResponse(
    Long id,
    String email,
    String username,
    String displayName,
    String bio,
    Boolean isEnabled
) {
}
