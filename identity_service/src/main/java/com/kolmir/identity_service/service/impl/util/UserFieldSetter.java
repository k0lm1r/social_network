package com.kolmir.identity_service.service.impl.util;

import com.kolmir.identity_service.dto.user.UserUpdateRequest;
import com.kolmir.identity_service.model.User;

import lombok.experimental.UtilityClass;


@UtilityClass
public class UserFieldSetter {
    public static User setFromUpdateRequest(User currentVersion, UserUpdateRequest request) {
        currentVersion.setBio(request.bio());
        currentVersion.setDisplayName(returnNotEmpty(request.displayName(), currentVersion.getDisplayName()));
        currentVersion.setEmail(returnNotEmpty(request.email(), currentVersion.getEmail()));
        currentVersion.setUsername(returnNotEmpty(request.username(), currentVersion.getUsername()));
        return currentVersion;

    }

    private static String returnNotEmpty(String str, String ifEmptyRes) {
        return str != null && !str.isBlank() ? str : ifEmptyRes;
    }
}
