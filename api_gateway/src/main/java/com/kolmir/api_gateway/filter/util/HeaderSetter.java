package com.kolmir.api_gateway.filter.util;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import com.kolmir.api_gateway.service.TokenValidationService;
import static com.kolmir.api_gateway.util.FilterConstants.*;
import com.kolmir.validate_token.UserResponse;

import lombok.RequiredArgsConstructor;


@Component
@RequiredArgsConstructor
public class HeaderSetter {
    private final TokenValidationService tokenValidationService;

    public ServerHttpRequest addUserData(ServerHttpRequest request, String jwt) {
        UserResponse userData = tokenValidationService.getUserFromToken(jwt);
        return request.mutate()
            .header(ID_HEADER, String.valueOf(userData.getId()))
            .header(USERNAME_HEADER, userData.getUsername())
            .header(EMAIL_HEADER, userData.getEmail())
            .header(ROLE_HEADER, userData.getRole())
            .build();
    }
}
