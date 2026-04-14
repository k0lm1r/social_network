package com.kolmir.api_gateway.testutil;

import lombok.experimental.UtilityClass;


@UtilityClass
public class GatewayTestConstants {

    public static final String AUTH_HEADER = "Authorization";
    public static final String USERNAME_HEADER = "X-User-Name";
    public static final String EMAIL_HEADER = "X-User-Email";
    public static final String ROLE_HEADER = "X-User-Role";

    public static final String EUREKA_ENABLED_PROPERTY = "eureka.client.enabled";
    public static final String DISCOVERY_ENABLED_PROPERTY = "spring.cloud.discovery.enabled";
    public static final String DISCOVERY_LOCATOR_ENABLED_PROPERTY = "spring.cloud.gateway.server.webflux.discovery.locator.enabled";
    public static final String ROUTE_ID_PROPERTY = "spring.cloud.gateway.server.webflux.routes[0].id";
    public static final String ROUTE_URI_PROPERTY = "spring.cloud.gateway.server.webflux.routes[0].uri";
    public static final String ROUTE_PREDICATE_PROPERTY = "spring.cloud.gateway.server.webflux.routes[0].predicates[0]";

    public static final String FALSE = "false";
    public static final String ROUTE_ID = "identity-route-test";
    public static final String ROUTE_PREDICATE = "Path=/api/users/**,/api/auth/**";
    public static final String ROOT_PATH = "/";

    public static final String PATH_USER_42 = "/api/users/42";
    public static final String PATH_AUTH_LOGIN = "/api/auth/login";
    public static final String PATH_UNMATCHED = "/unmatched/path";
    public static final String BASE_URL_PREFIX = "http://localhost:";

    public static final String RESPONSE_OK = "ok";
    public static final String RESPONSE_AUTHORIZED = "authorized";
    public static final String BEARER_TOKEN = "Bearer token-value";
    public static final String USERNAME_ALICE = "alice";
    public static final String EMAIL_ALICE = "alice@test.com";
    public static final String USER_ROLE = "USER";
}
