package com.kolmir.identity_service.dto;

public record UserRegisterResponse(
    UserAuthResponse auth,
    UserResponse user
) {}