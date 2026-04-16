package com.kolmir.identity_service.dto.auth;

import jakarta.validation.constraints.NotBlank;


public record RefreshTokenRequest (
    @NotBlank(message = "{blank}")
    String refreshToken
) {}
