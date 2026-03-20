package com.kolmir.identity_service.service.impl.util;

import com.kolmir.identity_service.dto.UserUpdateRequest;
import com.kolmir.identity_service.model.User;


public class UserFieldSetter {
    public static User setFromUpdateRequest(User currentVersion, UserUpdateRequest request) {
        currentVersion.setBio(returnNotEmpty(request.bio(), currentVersion.getBio()));
        currentVersion.setDisplayName(returnNotEmpty(request.displayName(), currentVersion.getDisplayName()));
        currentVersion.setEmail(returnNotEmpty(request.email(), currentVersion.getEmail()));
        currentVersion.setUsername(returnNotEmpty(request.username(), currentVersion.getUsername()));
        return currentVersion;

    }

    private static String returnNotEmpty(String str, String ifEmptyRes) {
        return str != null && !str.isBlank() ? str : ifEmptyRes;
    }
}
