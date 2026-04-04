package com.kolmir.identity_service.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class UserConstants {
    public final String USER_MAIN_URL = "/api/users";
    public final String USER_ID_URL = "/{id}";
    public final String USER_DISABLE_URL = "/disable" + USER_ID_URL;
    public final String USER_ID_NOT_FOUND = "User with this id was not found";
    public final String USER_DISABLING_MESSAGE = "You can't disable yourself";
}
