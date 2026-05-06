package com.kolmir.auth.header;

import java.util.Set;

import lombok.experimental.UtilityClass;


@UtilityClass
public class AuthHeaders {
    public static final String USER_ID = "X-User-ID";
    public static final String USER_ROLE = "X-User-Role";
    public static final String USER_EMAIL = "X-User-Email";
    public static final String USERNAME = "X-User-Name";

    public static final Set<String> ALL_HEADERS = Set.of(
        USER_ID,
        USER_ROLE,
        USER_EMAIL,
        USERNAME
    );
}
