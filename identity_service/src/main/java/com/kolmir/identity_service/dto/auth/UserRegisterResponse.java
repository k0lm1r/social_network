package com.kolmir.identity_service.dto.auth;

import com.kolmir.identity_service.dto.user.UserResponse;

public record UserRegisterResponse(
    UserAuthResponse auth,
    UserResponse user
) {}