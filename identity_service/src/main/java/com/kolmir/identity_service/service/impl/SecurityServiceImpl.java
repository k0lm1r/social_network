package com.kolmir.identity_service.service.impl;

import java.util.Map;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import com.kolmir.identity_service.dto.RefreshTokenRequest;
import com.kolmir.identity_service.dto.UserAuthRequest;
import com.kolmir.identity_service.dto.UserAuthResponse;
import com.kolmir.identity_service.dto.UserCreateRequest;
import com.kolmir.identity_service.dto.UserRegisterResponse;
import com.kolmir.identity_service.repository.UserRepository;
import com.kolmir.identity_service.service.SecurityService;
import com.kolmir.identity_service.service.UserService;
import com.kolmir.identity_service.service.UserAuthProvider;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class SecurityServiceImpl implements SecurityService {

    private final UserRepository userRepository;
    private final UserService userService;
    private final UserAuthProvider userAuthProvider; 

    @Override
    public boolean isCurrentUserOwner(Long id) {
        Jwt jwt = (Jwt) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        String keycloakId = jwt.getSubject();
        return userRepository.existsByIdAndKeycloakId(id, keycloakId);
    }

    @Override
    public UserRegisterResponse register(UserCreateRequest request) {
        UserAuthRequest auth = new UserAuthRequest(request.username(), request.password());
        return new UserRegisterResponse(login(auth), userService.save(request));
    }

    @Override
    public UserAuthResponse login(UserAuthRequest request) {
        Map<String, Object> keycloakResponse = userAuthProvider.getTokensForUser(request.username(), request.password());
        return authResponseToKeycloakResponse(keycloakResponse);
    }
    
    @Override
    public UserAuthResponse refresh(RefreshTokenRequest refreshToken) {
        Map<String, Object> keycloakResponse = userAuthProvider.refreshUserToken(refreshToken.refreshToken());
        return authResponseToKeycloakResponse(keycloakResponse);
    }

    private UserAuthResponse authResponseToKeycloakResponse(Map<String, Object> keycloakResponse) {
        String accessToken = (String)keycloakResponse.get("access_token");
        String refreshToken = (String)keycloakResponse.get("refresh_token");
        Integer accessExpiresIn = (Integer)keycloakResponse.get("expires_in");
        Integer refreshExpiresIn = (Integer)keycloakResponse.get("refresh_expires_in");

        return new UserAuthResponse(accessToken, accessExpiresIn, refreshToken, refreshExpiresIn);
    }
}
