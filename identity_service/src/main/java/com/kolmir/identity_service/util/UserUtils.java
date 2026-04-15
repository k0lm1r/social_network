package com.kolmir.identity_service.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class UserUtils {
    public final String USER_MAIN_URL = "/api/users";
    public final String USER_ID_URL = "/{id}";
    public final String USER_DISABLE_URL = "/disable" + USER_ID_URL;
    public final String CHANGE_ROLE_URL = "/change-role" + USER_ID_URL;
    public final String USER_ID_NOT_FOUND = "User with this id was not found";
    public final String USER_DISABLING_MESSAGE = "You can't disable yourself";
    public final String NO_PERMISIONS_EXCEPTION_MESSAGE = "No permision to set this role";
    public final String ROLE_CHANGING_EXCEPTION_MESSAGE = "Exception while changing role: ";
    public final String CREATING_EXCEPTION_MESSAGE = "User creating error: ";
    public final String EMAIL_IN_USE_MESSAGE = "This email is already in use";
    public final String USERNAME_IN_USE_MESSAGE = "This username is already in use";

    public String getShortBio(String bio) {
        return String.format("%s...", bio.substring(0, Math.min(10, bio.length())));
    }
}
