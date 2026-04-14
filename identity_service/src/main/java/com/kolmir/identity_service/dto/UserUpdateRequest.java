package com.kolmir.identity_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UserUpdateRequest (

    @Email(message = "{email.invalid}")
    @Size(min = 1, max = 255, message = "{size}")
    String email,

    @Size(min = 1, max = 50, message = "{size}")
    String username,

    @Size(min = 1, max = 50, message = "{size}")
    String displayName,

    @Size(max = 1000, message = "{size}")
    String bio
) {}
