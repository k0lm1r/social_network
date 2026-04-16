package com.kolmir.identity_service.service;

import com.kolmir.identity_service.dto.auth.RefreshTokenRequest;
import com.kolmir.identity_service.dto.auth.UserAuthRequest;
import com.kolmir.identity_service.dto.auth.UserAuthResponse;
import com.kolmir.identity_service.dto.auth.UserRegisterRequest;
import com.kolmir.identity_service.dto.auth.UserRegisterResponse;
import com.kolmir.identity_service.dto.user.UserResponse;


public interface SecurityService {
    public UserRegisterResponse register(UserRegisterRequest request);
    public UserAuthResponse login(UserAuthRequest request);
    public UserAuthResponse refresh(RefreshTokenRequest refreshToken);
    public boolean isCurrentUserOwner(Long id);
    public UserResponse getUserFromPrincipal();
}
