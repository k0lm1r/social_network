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
import com.kolmir.identity_service.dto.UserResponse;
import com.kolmir.identity_service.mapper.AuthMapper;
import com.kolmir.identity_service.mapper.UserMapper;
import com.kolmir.identity_service.model.User;
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
    private final AuthMapper authMapper;
    private final UserMapper userMapper;

    @Override
    public boolean isCurrentUserOwner(Long id) {
        String keycloakId = getKeycloakIdFromJwt();
        return userRepository.existsByIdAndKeycloakId(id, keycloakId);
    }

    @Override
    public UserRegisterResponse register(UserCreateRequest request) {
        UserAuthRequest auth = authMapper.userCreateRequestToUserAuthRequest(request);
        UserResponse userResponse = userService.save(request);
        return new UserRegisterResponse(authUser(auth), userResponse);
    }

    @Override
    public UserAuthResponse login(UserAuthRequest request) {
        return authUser(request);
    }
    
    @Override
    public UserAuthResponse refresh(RefreshTokenRequest refreshToken) {
        Map<String, Object> keycloakResponse = userAuthProvider.refreshUserToken(refreshToken.refreshToken());
        return authMapper.keycloakResponseToUserAuth(keycloakResponse);
    }
    
    @Override
    public UserResponse getUserFromPrincipal() {
        String keycloakId = getKeycloakIdFromJwt();
        User user = userRepository.getUserByKeycloakId(keycloakId);
        return userMapper.toUserResponse(user);
    }

    private UserAuthResponse authUser(UserAuthRequest request) {
        Map<String, Object> keycloakResponse = userAuthProvider.getTokensForUser(request.username(), request.password());
        return authMapper.keycloakResponseToUserAuth(keycloakResponse);
    }

    private String getKeycloakIdFromJwt() {
        Jwt jwt = (Jwt) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        return jwt.getSubject();
    }
}
