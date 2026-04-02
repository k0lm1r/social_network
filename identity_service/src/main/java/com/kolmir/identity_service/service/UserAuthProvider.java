package com.kolmir.identity_service.service;

import java.util.Map;

import com.kolmir.identity_service.dto.UserCreateRequest;
import com.kolmir.identity_service.model.User;


public interface UserAuthProvider {
    public String createUser(UserCreateRequest user);
    public void changeUserInfo(User user);
    public void disableUser(String userId);
    public Map<String, Object> getTokensForUser(String username, String password);
    public Map<String, Object> refreshUserToken(String refreshToken);
}
