package com.kolmir.security.provider;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.kolmir.auth.model.CurrentUser;
import static com.kolmir.security.util.CurrentUserUtil.*;


public class CurrentUserProvider {
    public CurrentUser getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            throw new IllegalStateException(USER_NOT_AUTHORIZE_MESSAGE);
        }

        Object principal = authentication.getPrincipal();
        if (!(principal instanceof CurrentUser currentUser)) {
            throw new IllegalStateException(ILLEGAL_PRINCIPAL_MESSAGE);
        }

        return currentUser;
    }

    public Long getCurrentUserId() {
        return getCurrentUser().id();
    }
}
