package com.kolmir.security.provider;

import org.springframework.security.core.context.SecurityContextHolder;

import com.kolmir.auth.model.CurrentUser;


public class CurrentUserProvider {
    public CurrentUser getCurrentUser() {
        return (CurrentUser)SecurityContextHolder.getContext()
            .getAuthentication()
            .getPrincipal();
    }

    public Long getCurrentUserId() {
        return getCurrentUser().id();
    }
}
