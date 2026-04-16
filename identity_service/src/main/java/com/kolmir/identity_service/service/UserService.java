package com.kolmir.identity_service.service;

import java.util.List;

import com.kolmir.identity_service.dto.auth.UserRegisterRequest;
import com.kolmir.identity_service.dto.user.UserChangeRoleRequest;
import com.kolmir.identity_service.dto.user.UserCreateRequest;
import com.kolmir.identity_service.dto.user.UserResponse;
import com.kolmir.identity_service.dto.user.UserUpdateRequest;


public interface UserService {
    public List<UserResponse> getAll();
    public UserResponse getById(Long id);
    public UserResponse saveRegisteredUser(UserRegisterRequest request);
    public UserResponse update(Long id, UserUpdateRequest request);
    public UserResponse disable(Long id);
    public UserResponse changeRole(Long id, UserChangeRoleRequest request);
    public UserResponse saveCreatedUser(UserCreateRequest request);
}
