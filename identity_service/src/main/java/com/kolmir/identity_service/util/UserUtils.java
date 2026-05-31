package com.kolmir.identity_service.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class UserUtils {
    public static final String USER_MAIN_URL = "/api/users";
    public static final String USER_ID_URL = "/{id}";
    public static final String USER_DISABLE_URL = "/disable" + USER_ID_URL;
    public static final String CHANGE_ROLE_URL = "/change-role" + USER_ID_URL;
    public static final String IS_USER_EXISTS_URL = USER_ID_URL + "/exists";
    public static final String USER_ID_NOT_FOUND = "User with this id was not found";
    public static final String USER_DISABLING_MESSAGE = "You can't disable yourself";
    public static final String NO_PERMISIONS_EXCEPTION_MESSAGE = "No permision to set this role";
    public static final String ROLE_CHANGING_EXCEPTION_MESSAGE = "Exception while changing role: ";
    public static final String CREATING_EXCEPTION_MESSAGE = "User creating error: ";
    public static final String EMAIL_IN_USE_MESSAGE = "This email is already in use";
    public static final String USERNAME_IN_USE_MESSAGE = "This username is already in use";

    public static String getShortBio(String bio) {
        if (bio == null || bio.length() < 10)
            return bio;
        return String.format("%s...", bio.substring(0, bio.length()));
    }
}
