package com.kolmir.identity_service.controller.api;

import com.kolmir.identity_service.dto.user.UserChangeRoleRequest;
import com.kolmir.identity_service.dto.user.UserCreateRequest;
import com.kolmir.identity_service.dto.user.UserResponse;
import com.kolmir.identity_service.dto.user.UserUpdateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;


@Tag(name = "Users", description = "User management endpoints")
@SecurityRequirement(name = "bearerAuth")
public interface UserControllerApi {
    @Operation(summary = "Get all users", description = "Returns all users. Requires ADMIN role")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Users fetched"),
        @ApiResponse(
            responseCode = "403",
            description = "Access denied",
            content = @Content(schema = @Schema(implementation = com.kolmir.identity_service.dto.exception.ErrorResponse.class))
        )
    })
    ResponseEntity<List<UserResponse>> getAll();

    @Operation(summary = "Get user by id", description = "Returns a user by id")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "User found"),
        @ApiResponse(
            responseCode = "404",
            description = "User not found",
            content = @Content(schema = @Schema(implementation = com.kolmir.identity_service.dto.exception.ErrorResponse.class))
        )
    })
    ResponseEntity<UserResponse> getById(@PathVariable Long id);

    @Operation(summary = "Update user", description = "Updates user fields by id")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "User updated"),
        @ApiResponse(
            responseCode = "400",
            description = "Validation error",
            content = @Content(schema = @Schema(implementation = com.kolmir.identity_service.dto.exception.ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "User not found",
            content = @Content(schema = @Schema(implementation = com.kolmir.identity_service.dto.exception.ErrorResponse.class))
        )
    })
    ResponseEntity<UserResponse> updateById(@PathVariable Long id, @RequestBody @Valid UserUpdateRequest request);

    @Operation(summary = "Create user", description = "Creates a new user")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "User created"),
        @ApiResponse(
            responseCode = "400",
            description = "Validation error",
            content = @Content(schema = @Schema(implementation = com.kolmir.identity_service.dto.exception.ErrorResponse.class))
        )
    })
    ResponseEntity<UserResponse> create(@RequestBody @Valid UserCreateRequest request);

    @Operation(summary = "Disable user", description = "Disables a user by id")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "User disabled"),
        @ApiResponse(
            responseCode = "403",
            description = "Access denied",
            content = @Content(schema = @Schema(implementation = com.kolmir.identity_service.dto.exception.ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "User not found",
            content = @Content(schema = @Schema(implementation = com.kolmir.identity_service.dto.exception.ErrorResponse.class))
        )
    })
    ResponseEntity<UserResponse> disable(@PathVariable Long id);

    @Operation(summary = "Change user role", description = "Changes role for a user by id. Requires MAIN_ADMIN role")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "User role changed"),
        @ApiResponse(
            responseCode = "403",
            description = "Access denied",
            content = @Content(schema = @Schema(implementation = com.kolmir.identity_service.dto.exception.ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "User not found",
            content = @Content(schema = @Schema(implementation = com.kolmir.identity_service.dto.exception.ErrorResponse.class))
        )
    })
    ResponseEntity<UserResponse> changeRole(@PathVariable Long id, @RequestBody @Valid UserChangeRoleRequest request);
}
