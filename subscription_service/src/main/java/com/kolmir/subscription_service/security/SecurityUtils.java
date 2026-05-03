package com.kolmir.subscription_service.security;

import org.springframework.security.core.context.SecurityContextHolder;

import com.kolmir.subscription_service.model.CurrentUser;

import lombok.experimental.UtilityClass;


@UtilityClass
public class SecurityUtils {
    public static CurrentUser getCurrentUser() {
        return (CurrentUser)SecurityContextHolder.getContext()
            .getAuthentication()
            .getPrincipal();
    }

    public static Long getCurrentUserId() {
        return getCurrentUser().id();
    }
}
