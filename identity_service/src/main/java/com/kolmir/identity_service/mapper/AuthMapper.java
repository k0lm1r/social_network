package com.kolmir.identity_service.mapper;

import java.util.Map;

import org.mapstruct.Mapper;

import com.kolmir.identity_service.dto.auth.UserAuthRequest;
import com.kolmir.identity_service.dto.auth.UserAuthResponse;
import com.kolmir.identity_service.dto.auth.UserRegisterRequest;

@Mapper(componentModel = "spring")
public interface AuthMapper {
    public default UserAuthResponse keycloakResponseToUserAuth(Map<String, Object> keycloakResponse) {
        String accessToken = (String)keycloakResponse.get("access_token");
        String refreshToken = (String)keycloakResponse.get("refresh_token");
        Integer accessExpiresIn = (Integer)keycloakResponse.get("expires_in");
        Integer refreshExpiresIn = (Integer)keycloakResponse.get("refresh_expires_in");

        return new UserAuthResponse(accessToken, accessExpiresIn, refreshToken, refreshExpiresIn);
    }

    public UserAuthRequest userCreateRequestToUserAuthRequest(UserRegisterRequest request);
}