package com.kolmir.identity_service.dto.auth;


import com.kolmir.logging.sanitizer.Sensitive;

import jakarta.validation.constraints.NotBlank;


public record RefreshTokenRequest (
    @NotBlank(message = "{blank}")
    @Sensitive String refreshToken
) {}
