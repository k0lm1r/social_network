package com.kolmir.identity_service.service;

import java.util.List;

import com.kolmir.identity_service.dto.UserCreateRequest;
import com.kolmir.identity_service.dto.UserResponse;
import com.kolmir.identity_service.dto.UserUpdateRequest;

public interface UserService {
    public List<UserResponse> getAll();
    public UserResponse getById(Long id);
    public UserResponse save(UserCreateRequest request);
    public UserResponse update(Long id, UserUpdateRequest request);
    public UserResponse disable(Long id);
}
