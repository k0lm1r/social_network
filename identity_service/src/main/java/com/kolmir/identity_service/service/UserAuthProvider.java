package com.kolmir.identity_service.service;

import java.util.UUID;

import com.kolmir.identity_service.dto.UserCreateRequest;
import com.kolmir.identity_service.model.User;


public interface UserAuthProvider {
    public String createUser(UserCreateRequest user);
    public void changeUserInfo(User user);
    public void disableUser(UUID userId);
}
