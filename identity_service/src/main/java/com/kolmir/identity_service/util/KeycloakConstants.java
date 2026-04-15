package com.kolmir.identity_service.util;

import lombok.experimental.UtilityClass;


@UtilityClass
public class KeycloakConstants {
    public final String ROLES_LIST_NAME = "roles";
    public final String REALM_ACCESS_NAME = "realm_access";
    public final String CLAIM_NAME = "preferred_username";
    public final String USER_ALREADY_EXISTS_MESSAGE = "This user is already exists";
    public final String USER_CREATING_EXCEPTION_TEMPLATE = "User creating exception with status %d";
    public final String LOCATION_HEADER_NAME = "Location";
    public final String UNAUTHORIZED_EXCEPTION_MESSAGE = "Username or password isn't correct";
    public final String BAD_REQUEST_EXCEPTION_MESSAGE = "Invalid request";
    public final String INVALID_GRANT_TEMPLATE = "Invalid grant: %d";
    public final String INVALID_GRANT = "invalid_grant";
}
