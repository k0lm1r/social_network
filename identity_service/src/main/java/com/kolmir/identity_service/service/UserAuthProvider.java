package com.kolmir.identity_service.service;

import java.util.Map;

import com.kolmir.identity_service.dto.auth.UserRegisterRequest;
import com.kolmir.identity_service.model.User;


public interface UserAuthProvider {
    public String createUser(UserRegisterRequest user);
    public void changeUserInfo(User user);
    public void disableUser(String userId);
    public Map<String, Object> getTokensForUser(String username, String password);
    public Map<String, Object> refreshUserToken(String refreshToken);
    public void deleteUser(String userId);
    public void changeUserRole(String userId, String oldRole, String newRole);
}
