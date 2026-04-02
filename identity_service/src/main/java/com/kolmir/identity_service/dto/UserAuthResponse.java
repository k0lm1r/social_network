package com.kolmir.identity_service.dto;


public record UserAuthResponse (
    String accessToken,
    Integer accessExpiresIn,
    String refreshToken,
    Integer refreshExpiresIn
) {}
