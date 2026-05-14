package com.kolmir.logging.util;

import java.util.Set;
import java.util.regex.Pattern;

import lombok.experimental.UtilityClass;


@UtilityClass
public class AuthUtils {
    public static final String REDACTED_MESSAGE = "[REDACTED]";
    public static final String CYCLE_MESSAGE = "[CYCLE]";
    public static final Pattern BEARER = Pattern.compile("(?i)^Bearer\\s+.+$");
    public static final Set<String> SENSITIVE_KEYS = Set.of(
        "password", "token", "accessToken", "refreshToken", "secret", "authorization"
    );    
}
