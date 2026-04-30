package com.kolmir.subscription_service.util;

import lombok.experimental.UtilityClass;


@UtilityClass
public class SubscriptionServiceConstants {
    public static final String ID_URL = "/{id}";
    public static final String USER_ID_URL = "/{userId}";
    public static final String ID_HEADER = "X-User-ID";
    public static final String USERNAME_HEADER = "X-User-Name";
    public static final String EMAIL_HEADER = "X-User-Email";
    public static final String ROLE_HEADER = "X-User-Role";
    public static final String ADMIN_ROLE = "ADMIN";
    public static final String USER_ROLE = "USER";
    public static final String MAIN_ADMIN_ROLE = "MAIN_ADMIN";
    public static final String INVALID_USER_DATA_MESSAGE = "Invalid user data headers";
    public static final String IS_USER_EXISTS_URL = "/api/users/{id}/exists";
    public static final String USER_NOT_EXISTS_MESSAGE = "User with this id does not exists";
    public static final String USER_ID_WAS_NOT_VALIDATED_EXCEPTION = "The user ID was not verified due to lack of access to the external service";
}
