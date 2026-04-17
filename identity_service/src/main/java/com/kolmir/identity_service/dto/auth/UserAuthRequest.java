package com.kolmir.identity_service.dto.auth;

import com.kolmir.identity_service.logging.Sensitive;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


public record UserAuthRequest (
    @Size(min = 1, max = 50, message = "{size}")
    String username,

    @NotBlank(message = "{blank}")
    @Size(min = 4, message = "{size}")
    @Sensitive String password
) {}