package com.kolmir.identity_service.util;

import java.security.SecureRandom;

import com.kolmir.identity_service.dto.UserAuthRequest;
import com.kolmir.identity_service.dto.UserAuthResponse;
import com.kolmir.identity_service.dto.UserRegisterRequest;
import com.kolmir.identity_service.dto.UserRegisterResponse;

import lombok.experimental.UtilityClass;

@UtilityClass
public class AuthUtils {
    public static final String AUTH_PATH = "/api/auth";
    public static final String LOGIN_PATH = "/login";
    public static final String REGISTER_PATH = "/register"; 
    public static final String REFRESH_PATH = "/refresh";
    public static final String REDACTED_MESSAGE = "[REDACTED]";
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%";

    public static String generatePassword(int length) {
        StringBuilder password = new StringBuilder(length);
        SecureRandom random = new SecureRandom();

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(CHARACTERS.length());
            password.append(CHARACTERS.charAt(index));
        }

        return password.toString();
    }

    public UserAuthResponse getSafeAuthResponse(UserAuthResponse response) {
        return new UserAuthResponse(
                    REDACTED_MESSAGE, 
                    response.accessExpiresIn(), 
                    REDACTED_MESSAGE, response.refreshExpiresIn()
                );
    }
    
    public UserAuthRequest getSafeAuthRequest(UserAuthRequest request) {
        return new UserAuthRequest(request.username(), REDACTED_MESSAGE);
    }

    public UserRegisterResponse getSafeRegisterResponse (UserRegisterResponse response) {
        return new UserRegisterResponse(getSafeAuthResponse(response.auth()), response.user());
    }

    public UserRegisterRequest getSafeRegisterRequest (UserRegisterRequest request) {
        return new UserRegisterRequest (
                request.email(), 
                request.username(), 
                REDACTED_MESSAGE, 
                request.displayName(), 
                UserUtils.getShortBio(request.bio())
        );
    }
}
