package com.kolmir.identity_service.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;


public record UserCreateRequest (
    @Email(message = "{email.invalid}")
    @Size(min = 1, max = 255, message = "{size}")
    @NotEmpty(message = "{empty}")
    String email,

    @Size(min = 1, max = 50, message = "{size}")
    String username,

    @NotBlank(message = "{blank}")
    @Size(min = 1, max = 50, message = "{size}")
    String displayName,

    @NotNull(message = "{notnull}")
    @Pattern(regexp = "ADMIN|USER", message = "{userrole}")
    String role
) {}
