package com.kolmir.identity_service.controller.api;

import com.kolmir.identity_service.dto.auth.RefreshTokenRequest;
import com.kolmir.identity_service.dto.auth.UserAuthRequest;
import com.kolmir.identity_service.dto.auth.UserAuthResponse;
import com.kolmir.identity_service.dto.auth.UserRegisterRequest;
import com.kolmir.identity_service.dto.auth.UserRegisterResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;


@Tag(name = "Auth", description = "Authentication endpoints")
public interface AuthControllerApi {
    @Operation(summary = "User login", description = "Authenticates user and returns access and refresh tokens")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "User authenticated"),
        @ApiResponse(
            responseCode = "401",
            description = "Invalid credentials",
            content = @Content(schema = @Schema(implementation = com.kolmir.identity_service.dto.exception.ErrorResponse.class))
        )
    })
    ResponseEntity<UserAuthResponse> loginUser(@RequestBody @Valid UserAuthRequest request);

    @Operation(summary = "User registration", description = "Creates a new user account")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "User registered"),
        @ApiResponse(
            responseCode = "400",
            description = "Validation error",
            content = @Content(schema = @Schema(implementation = com.kolmir.identity_service.dto.exception.ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "409",
            description = "User already exists",
            content = @Content(schema = @Schema(implementation = com.kolmir.identity_service.dto.exception.ErrorResponse.class))
        )
    })
    ResponseEntity<UserRegisterResponse> registerUser(@RequestBody @Valid UserRegisterRequest request);

    @Operation(summary = "Refresh tokens", description = "Returns new token pair by refresh token")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Tokens refreshed"),
        @ApiResponse(
            responseCode = "401",
            description = "Refresh token is invalid or expired",
            content = @Content(schema = @Schema(implementation = com.kolmir.identity_service.dto.exception.ErrorResponse.class))
        )
    })
    ResponseEntity<UserAuthResponse> refreshUserToken(@RequestBody @Valid RefreshTokenRequest refreshToken);
}
