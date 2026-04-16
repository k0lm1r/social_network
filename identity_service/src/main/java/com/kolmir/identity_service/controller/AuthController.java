package com.kolmir.identity_service.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kolmir.identity_service.controller.api.AuthControllerApi;
import com.kolmir.identity_service.dto.auth.RefreshTokenRequest;
import com.kolmir.identity_service.dto.auth.UserAuthRequest;
import com.kolmir.identity_service.dto.auth.UserAuthResponse;
import com.kolmir.identity_service.dto.auth.UserRegisterRequest;
import com.kolmir.identity_service.dto.auth.UserRegisterResponse;
import com.kolmir.identity_service.service.SecurityService;

import jakarta.validation.Valid;

import static com.kolmir.identity_service.util.AuthUtils.*;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequiredArgsConstructor
@RequestMapping(AUTH_PATH)
public class AuthController implements AuthControllerApi {
    private final SecurityService securityService;

    @Override
    @PostMapping(LOGIN_PATH)
    public ResponseEntity<UserAuthResponse> loginUser(@RequestBody @Valid UserAuthRequest request) {
        return ResponseEntity.ok(securityService.login(request));
    }
    
    @Override
    @PostMapping(REGISTER_PATH)
    public ResponseEntity<UserRegisterResponse> registerUser(@RequestBody @Valid UserRegisterRequest request) {
        return ResponseEntity.ok(securityService.register(request));
    }

    @Override
    @PostMapping(REFRESH_PATH)
    public ResponseEntity<UserAuthResponse> refreshUserToken(@RequestBody @Valid RefreshTokenRequest refreshToken) {
        return ResponseEntity.ok(securityService.refresh(refreshToken));
    }
}
