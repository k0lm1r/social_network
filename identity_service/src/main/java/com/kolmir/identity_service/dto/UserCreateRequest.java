package com.kolmir.identity_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public record UserCreateRequest (
    @Email(message = "{email.invalid}")
    @NotEmpty(message = "{empty}")
    String email,

    @Size(min = 1, max = 50, message = "{size}")
    String username,

    @NotBlank(message = "{blank}")
    @Size(min = 1, max = 50, message = "{size}")
    String displayName,

    @Size(max = 1000, message = "{size}")
    String bio
) {
}
