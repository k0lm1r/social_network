package com.kolmir.identity_service.util;

import lombok.experimental.UtilityClass;


@UtilityClass
public class KeycloakConstants {
    public static final String ROLES_LIST_NAME = "roles";
    public static final String REALM_ACCESS_NAME = "realm_access";
    public static final String CLAIM_NAME = "preferred_username";
    public static final String USER_ALREADY_EXISTS_MESSAGE = "This user is already exists";
    public static final String USER_CREATING_EXCEPTION_TEMPLATE = "User creating exception with status %d";
    public static final String LOCATION_HEADER_NAME = "Location";
    public static final String UNAUTHORIZED_EXCEPTION_MESSAGE = "Username or password isn't correct";
    public static final String BAD_REQUEST_EXCEPTION_MESSAGE = "Invalid request";
    public static final String USER_DISABLED_MESSAGE = "User was disabled";
    public static final String INVALID_GRANT = "invalid_grant";
    public static final String ERROR_DESCRIPTION = "error_description";
}
