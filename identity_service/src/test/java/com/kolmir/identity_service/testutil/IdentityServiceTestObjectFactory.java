package com.kolmir.identity_service.testutil;

import com.kolmir.identity_service.dto.RefreshTokenRequest;
import com.kolmir.identity_service.dto.UserAuthRequest;
import com.kolmir.identity_service.dto.UserAuthResponse;
import com.kolmir.identity_service.dto.UserRegisterRequest;
import com.kolmir.identity_service.dto.UserResponse;
import com.kolmir.identity_service.dto.UserUpdateRequest;
import com.kolmir.identity_service.model.User;
import com.kolmir.identity_service.model.UserRole;

import lombok.experimental.UtilityClass;

import java.util.Map;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.security.oauth2.jwt.Jwt;


@UtilityClass
public class IdentityServiceTestObjectFactory {
    public static User user(
            Long id,
            String keycloakId,
            String email,
            String username,
            String displayName,
            String bio,
            boolean enabled
    ) {
        User user = new User();
        user.setId(id);
        user.setKeycloakId(keycloakId);
        user.setEmail(email);
        user.setUsername(username);
        user.setDisplayName(displayName);
        user.setBio(bio);
        user.setIsEnabled(enabled);
        return user;
    }

    public static UserRegisterRequest userCreateRequest(
            String email,
            String username,
            String password,
            String displayName,
            String bio
    ) {
        return new UserRegisterRequest(email, username, password, displayName, bio);
    }

    public static UserUpdateRequest userUpdateRequest(
            String email,
            String username,
            String displayName,
            String bio
    ) {
        return new UserUpdateRequest(email, username, displayName, bio);
    }

    public static UserResponse userResponse(
            Long id,
            String email,
            String username,
            String displayName,
            String bio,
            UserRole role,
            boolean enabled
    ) {
        return new UserResponse(id, email, username, displayName, bio, role, enabled);
    }

    public static UserAuthRequest userAuthRequest(String username, String password) {
        return new UserAuthRequest(username, password);
    }

    public static UserAuthResponse userAuthResponse(String accessToken, int expiresIn, String refreshToken, int refreshExpiresIn) {
        return new UserAuthResponse(accessToken, expiresIn, refreshToken, refreshExpiresIn);
    }

    public static RefreshTokenRequest refreshTokenRequest(String refreshToken) {
        return new RefreshTokenRequest(refreshToken);
    }

    public static Map<String, Object> keycloakTokenResponse(
            String accessToken,
            String refreshToken,
            int expiresIn,
            int refreshExpiresIn
    ) {
        return Map.of(
                IdentityServiceTestConstants.KEY_ACCESS_TOKEN, accessToken,
                IdentityServiceTestConstants.KEY_REFRESH_TOKEN, refreshToken,
                IdentityServiceTestConstants.KEY_EXPIRES_IN, expiresIn,
                IdentityServiceTestConstants.KEY_REFRESH_EXPIRES_IN, refreshExpiresIn
        );
    }

    public static Jwt jwtWithSubject(String subject) {
        return Jwt.withTokenValue(IdentityServiceTestConstants.JWT_TOKEN_VALUE)
                .header(IdentityServiceTestConstants.JWT_HEADER_ALG, IdentityServiceTestConstants.JWT_ALG_NONE)
                .subject(subject)
                .claim(IdentityServiceTestConstants.JWT_SUB_CLAIM, subject)
                .build();
    }

    public static UserRepresentation userRepresentation(String username, String email, boolean enabled) {
        UserRepresentation representation = new UserRepresentation();
        representation.setUsername(username);
        representation.setEmail(email);
        representation.setEnabled(enabled);
        return representation;
    }

    public static RoleRepresentation roleRepresentation(String roleName) {
        RoleRepresentation representation = new RoleRepresentation();
        representation.setName(roleName);
        return representation;
    }
}
