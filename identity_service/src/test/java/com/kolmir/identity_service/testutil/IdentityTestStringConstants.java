package com.kolmir.identity_service.testutil;

import lombok.experimental.UtilityClass;


@UtilityClass
public class IdentityTestStringConstants {

    public static final String KEYCLOAK_ID = "kc-id";
    public static final String KEYCLOAK_ID_ALT = "kc-alt";
    public static final String KEYCLOAK_ID_CREATED = "kc-created";

    public static final String EMAIL = "user@test.com";
    public static final String EMAIL_NEW = "new@test.com";
    public static final String USERNAME = "user";
    public static final String USERNAME_NEW = "user_new";
    public static final String DISPLAY_NAME = "Display Name";
    public static final String DISPLAY_NAME_NEW = "New Display Name";
    public static final String BIO = "bio";
    public static final String BIO_NEW = "new bio";
    public static final String PASSWORD = "pass1234";
    public static final String EMPTY = "";

    public static final String ACCESS_TOKEN = "access-token";
    public static final String REFRESH_TOKEN = "refresh-token";
    public static final String REFRESH_TOKEN_VALUE = "refresh-token-value";
    public static final String USER_CREATING_EXCEPTION_500 = "User creating exception with status 500";

    public static final String REALM = "test-realm";
    public static final String SERVER_URL = "http://localhost:8080";
    public static final String CLIENT_ID = "client";
    public static final String CLIENT_SECRET = "secret";
    public static final String LOCATION_HEADER = "http://localhost:8080/admin/realms/test/users/kc-created";
    public static final String USER_ROLE = "USER";

    public static final String KEY_ACCESS_TOKEN = "access_token";
    public static final String KEY_REFRESH_TOKEN = "refresh_token";
    public static final String KEY_EXPIRES_IN = "expires_in";
    public static final String KEY_REFRESH_EXPIRES_IN = "refresh_expires_in";
    public static final String JWT_TOKEN_VALUE = "token";
    public static final String JWT_HEADER_ALG = "alg";
    public static final String JWT_ALG_NONE = "none";
    public static final String JWT_SUB_CLAIM = "sub";
}
