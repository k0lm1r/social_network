package com.kolmir.identity_service.util;

import java.security.SecureRandom;
import java.util.Set;
import java.util.regex.Pattern;

import lombok.experimental.UtilityClass;

@UtilityClass
public class AuthUtils {
    public static final String AUTH_PATH = "/api/auth";
    public static final String LOGIN_PATH = "/login";
    public static final String REGISTER_PATH = "/register"; 
    public static final String REFRESH_PATH = "/refresh";
    public static final String REDACTED_MESSAGE = "[REDACTED]";
    public static final String CYCLE_MESSAGE = "[CYCLE]";
    public static final Pattern BEARER = Pattern.compile("(?i)^Bearer\\s+.+$");
    public static final Set<String> SENSITIVE_KEYS = Set.of(
        "password", "token", "accessToken", "refreshToken", "secret", "authorization"
    );
    private final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%";

    public static String generatePassword(int length) {
        StringBuilder password = new StringBuilder(length);
        SecureRandom random = new SecureRandom();

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(CHARACTERS.length());
            password.append(CHARACTERS.charAt(index));
        }

        return password.toString();
    }
}
