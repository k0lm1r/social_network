package com.kolmir.identity_service.dto.auth;

import com.kolmir.identity_service.logging.Sensitive;

import jakarta.validation.constraints.NotBlank;


public record RefreshTokenRequest (
    @NotBlank(message = "{blank}")
    @Sensitive String refreshToken
) {}
