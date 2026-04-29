package com.kolmir.identity_service.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kolmir.identity_service.controller.api.UserControllerApi;
import com.kolmir.identity_service.dto.user.UserChangeRoleRequest;
import com.kolmir.identity_service.dto.user.UserCreateRequest;
import com.kolmir.identity_service.dto.user.UserResponse;
import com.kolmir.identity_service.dto.user.UserUpdateRequest;
import com.kolmir.identity_service.service.UserService;
import static com.kolmir.identity_service.util.UserUtils.*;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;


@RestController
@RequiredArgsConstructor
@RequestMapping(USER_MAIN_URL)
public class UserController implements UserControllerApi {
    private final UserService userService;
    
    @Override
    @GetMapping
    public ResponseEntity<Page<UserResponse>> getAll(Pageable pageable) {
        return ResponseEntity.ok(userService.getAll(pageable));
    }
    
    @Override
    @GetMapping(USER_ID_URL)
    public ResponseEntity<UserResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getById(id));
    }
    
    @Override
    @PutMapping(USER_ID_URL)
    @PreAuthorize("@securityServiceImpl.isCurrentUserOwner(#id) || hasAnyRole('ADMIN', 'MAIN_ADMIN')")
    public ResponseEntity<UserResponse> updateById(@PathVariable Long id, @RequestBody @Valid UserUpdateRequest request) {
        return ResponseEntity.ok(userService.update(id, request));
    }

    @Override
    @PostMapping
    public ResponseEntity<UserResponse> create(@RequestBody @Valid UserCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.saveCreatedUser(request));
    }

    @Override
    @PatchMapping(USER_DISABLE_URL)
    @PreAuthorize("!@securityServiceImpl.isCurrentUserOwner(#id)")
    public ResponseEntity<UserResponse> disable(@PathVariable Long id) {
        return ResponseEntity.ok(userService.disable(id));
    }

    @Override
    @PatchMapping(CHANGE_ROLE_URL)
    @PreAuthorize("!@securityServiceImpl.isCurrentUserOwner(#id)")
    public ResponseEntity<UserResponse> changeRole(@PathVariable Long id, @RequestBody @Valid UserChangeRoleRequest request) {
        return ResponseEntity.ok(userService.changeRole(id, request));
    }

    
}
