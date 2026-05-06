package com.kolmir.security.util;

import static com.kolmir.auth.header.AuthHeaders.ALL_HEADERS;
import static com.kolmir.auth.header.AuthHeaders.USERNAME;
import static com.kolmir.auth.header.AuthHeaders.USER_EMAIL;
import static com.kolmir.auth.header.AuthHeaders.USER_ID;
import static com.kolmir.auth.header.AuthHeaders.USER_ROLE;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.util.StringUtils;

import com.kolmir.auth.model.CurrentUser;
import com.kolmir.auth.model.UserRole;

import jakarta.servlet.http.HttpServletRequest;
import lombok.experimental.UtilityClass;


@UtilityClass
public class CurrentUserUtil {
    public static Optional<CurrentUser> extractCurrentUser(HttpServletRequest request) {
        Map<String, String> allHeaders = new HashMap<>();
        ALL_HEADERS.forEach(header -> allHeaders.put(header, request.getHeader(header)));

        if (!isHeadersValid(allHeaders))
            return Optional.empty();
        return Optional.of(getCurrentUser(allHeaders));
    }
    
    private boolean isHeadersValid(Map<String, String> allHeaders) {
        if (allHeaders.values().stream().noneMatch(StringUtils::hasText))
            return false;
        try {
            Long.valueOf(allHeaders.get(USER_ID));
            return true;
        } catch (Exception _) {
            return false;
        }
    }

    private CurrentUser getCurrentUser(Map<String, String> allHeaders) {
        return new CurrentUser(
            Long.valueOf(allHeaders.get(USER_ID)), 
            allHeaders.get(USERNAME), 
            allHeaders.get(USER_EMAIL), 
            UserRole.valueOf(allHeaders.get(USER_ROLE))
        );
    }
}