package com.kolmir.api_gateway.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class FilterConstants {
    public static final String AUTORIZATION_HEADER = "Authorization";
    public static final String USERNAME_HEADER = "X-User-Name";
    public static final String EMAIL_HEADER = "X-User-Email";
    public static final String ROLE_HEADER = "X-User-Role";
    public static final String REDACTERED_TOKEN = "[REDACTERED]";
}
