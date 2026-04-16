package com.kolmir.identity_service.dto.user;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record UserChangeRoleRequest (
    @NotNull(message = "{notnull}")
    @Pattern(regexp = "ADMIN|USER", message = "{userrole}")
    String newRole
) {}