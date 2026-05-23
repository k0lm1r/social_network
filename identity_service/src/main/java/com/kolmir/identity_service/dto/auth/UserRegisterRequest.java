package com.kolmir.identity_service.dto.auth;

import com.kolmir.logging.sanitizer.Sensitive;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;


public record UserRegisterRequest (
    @Email(message = "{email.invalid}")
    @Size(min = 1, max = 255, message = "{size}")
    @NotEmpty(message = "{empty}")
    String email,

    @Size(min = 1, max = 50, message = "{size}")
    String username,

    @Sensitive
    @NotBlank(message = "{blank}")
    @Size(min = 4, message = "{size}")
    String password,

    @NotBlank(message = "{blank}")
    @Size(min = 1, max = 50, message = "{size}")
    String displayName,

    @Size(max = 1000, message = "{size}")
    String bio
) {
}
