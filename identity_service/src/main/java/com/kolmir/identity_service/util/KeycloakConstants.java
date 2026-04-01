package com.kolmir.identity_service.util;

import lombok.experimental.UtilityClass;


@UtilityClass
public class KeycloakConstants {
    public final String ROLES_LIST_NAME = "spring_roles";
    public final String CLAIM_NAME = "preferred_username";
    public final String USER_ALREADY_EXISTS_MESSAGE = "This user is already exists";
    public final String USER_CREATING_EXCEPTION_TEMPLATE = "User creating exception with status %d";
    public final String LOCATION_HEADER_NAME = "Location";
}
