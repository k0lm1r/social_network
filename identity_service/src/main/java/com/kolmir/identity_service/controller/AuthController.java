package com.kolmir.identity_service.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kolmir.identity_service.dto.RefreshTokenRequest;
import com.kolmir.identity_service.dto.UserAuthRequest;
import com.kolmir.identity_service.dto.UserAuthResponse;
import com.kolmir.identity_service.dto.UserCreateRequest;
import com.kolmir.identity_service.dto.UserRegisterResponse;
import com.kolmir.identity_service.service.SecurityService;
import static com.kolmir.identity_service.util.AuthConstants.*;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequiredArgsConstructor
@RequestMapping(AUTH_PATH)
public class AuthController {
    private final SecurityService securityService;

    @PostMapping(LOGIN_PATH)
    public ResponseEntity<UserAuthResponse> loginUser(@RequestBody UserAuthRequest request) {
        return ResponseEntity.ok(securityService.login(request));
    }
    
    @PostMapping(REGISTER_PATH)
    public ResponseEntity<UserRegisterResponse> registerUser(@RequestBody UserCreateRequest request) {
        return ResponseEntity.ok(securityService.register(request));
    }

    @PostMapping(REFRESH_PATH)
    public ResponseEntity<UserAuthResponse> refreshUserToken(@RequestBody RefreshTokenRequest refreshToken) {
        return ResponseEntity.ok(securityService.refresh(refreshToken));
    }
}
