package com.kolmir.identity_service.service;

import com.kolmir.identity_service.dto.RefreshTokenRequest;
import com.kolmir.identity_service.dto.UserAuthRequest;
import com.kolmir.identity_service.dto.UserAuthResponse;
import com.kolmir.identity_service.dto.UserCreateRequest;
import com.kolmir.identity_service.dto.UserRegisterResponse;

public interface SecurityService {
    public UserRegisterResponse register(UserCreateRequest request);
    public UserAuthResponse login(UserAuthRequest request);
    public UserAuthResponse refresh(RefreshTokenRequest refreshToken);
    public boolean isCurrentUserOwner(Long id);
}
