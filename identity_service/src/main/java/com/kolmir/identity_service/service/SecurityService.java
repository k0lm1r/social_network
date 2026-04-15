package com.kolmir.identity_service.service;

import com.kolmir.identity_service.dto.RefreshTokenRequest;
import com.kolmir.identity_service.dto.UserAuthRequest;
import com.kolmir.identity_service.dto.UserAuthResponse;
import com.kolmir.identity_service.dto.UserRegisterRequest;
import com.kolmir.identity_service.dto.UserRegisterResponse;
import com.kolmir.identity_service.dto.UserResponse;


public interface SecurityService {
    public UserRegisterResponse register(UserRegisterRequest request);
    public UserAuthResponse login(UserAuthRequest request);
    public UserAuthResponse refresh(RefreshTokenRequest refreshToken);
    public boolean isCurrentUserOwner(Long id);
    public UserResponse getUserFromPrincipal();
}
