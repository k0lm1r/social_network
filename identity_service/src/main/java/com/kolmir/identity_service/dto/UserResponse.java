package com.kolmir.identity_service.dto;

import java.util.UUID;


public record UserResponse(
    Long id,
    UUID keycloakId,
    String email,
    String username,
    String displayName,
    String bio
) {
}
