package com.kolmir.identity_service.dto.auth;

import com.kolmir.logging.sanitizer.Sensitive;


public record UserAuthResponse (
    @Sensitive
    String accessToken,
    
    Integer accessExpiresIn,

    @Sensitive
    String refreshToken,

    Integer refreshExpiresIn
) {}
