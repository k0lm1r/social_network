package com.kolmir.api_gateway;

import static com.kolmir.api_gateway.testutil.GatewayTestConstants.AUTH_HEADER;
import static com.kolmir.api_gateway.testutil.GatewayTestConstants.DISCOVERY_ENABLED_PROPERTY;
import static com.kolmir.api_gateway.testutil.GatewayTestConstants.DISCOVERY_LOCATOR_ENABLED_PROPERTY;
import static com.kolmir.api_gateway.testutil.GatewayTestConstants.EMAIL_ALICE;
import static com.kolmir.api_gateway.testutil.GatewayTestConstants.EMAIL_HEADER;
import static com.kolmir.api_gateway.testutil.GatewayTestConstants.EUREKA_ENABLED_PROPERTY;
import static com.kolmir.api_gateway.testutil.GatewayTestConstants.FALSE;
import static com.kolmir.api_gateway.testutil.GatewayTestConstants.PATH_AUTH_LOGIN;
import static com.kolmir.api_gateway.testutil.GatewayTestConstants.PATH_UNMATCHED;
import static com.kolmir.api_gateway.testutil.GatewayTestConstants.PATH_USER_42;
import static com.kolmir.api_gateway.testutil.GatewayTestConstants.RESPONSE_AUTHORIZED;
import static com.kolmir.api_gateway.testutil.GatewayTestConstants.RESPONSE_OK;
import static com.kolmir.api_gateway.testutil.GatewayTestConstants.ROLE_HEADER;
import static com.kolmir.api_gateway.testutil.GatewayTestConstants.ROUTE_ID;
import static com.kolmir.api_gateway.testutil.GatewayTestConstants.ROUTE_ID_PROPERTY;
import static com.kolmir.api_gateway.testutil.GatewayTestConstants.ROUTE_PREDICATE;
import static com.kolmir.api_gateway.testutil.GatewayTestConstants.ROUTE_PREDICATE_PROPERTY;
import static com.kolmir.api_gateway.testutil.GatewayTestConstants.ROUTE_URI_PROPERTY;
import static com.kolmir.api_gateway.testutil.GatewayTestConstants.ROOT_PATH;
import static com.kolmir.api_gateway.testutil.GatewayTestConstants.USERNAME_ALICE;
import static com.kolmir.api_gateway.testutil.GatewayTestConstants.USERNAME_HEADER;
import static com.kolmir.api_gateway.testutil.GatewayTestConstants.USER_ROLE;
import static com.kolmir.api_gateway.testutil.GatewayTestConstants.BEARER_TOKEN;
import static com.kolmir.api_gateway.testutil.GatewayTestObjectFactory.httpGetRequest;
import static com.kolmir.api_gateway.testutil.GatewayTestObjectFactory.httpGetRequestWithHeader;
import static com.kolmir.api_gateway.testutil.GatewayTestObjectFactory.mockResponse;
import static com.kolmir.api_gateway.testutil.GatewayTestObjectFactory.userResponse;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.io.UncheckedIOException;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.boot.test.web.server.LocalServerPort;

import com.kolmir.api_gateway.service.TokenValidationService;
import com.kolmir.validate_token.UserResponse;

import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GatewayIntegrationTest {

    private static MockWebServer backend;

    @Autowired
    private TokenValidationService tokenValidationService;

    @LocalServerPort
    private int gatewayPort;

    private final HttpClient httpClient = HttpClient.newHttpClient();

    @AfterAll
    static void stopBackend() throws IOException {
        if (backend != null) {
            backend.shutdown();
        }
    }

    @BeforeEach
    void resetBackend() throws InterruptedException {
        while (backend.takeRequest(10, TimeUnit.MILLISECONDS) != null) {
        }
    }

    @DynamicPropertySource
    static void registerTestProperties(DynamicPropertyRegistry registry) {
        if (backend == null) {
            backend = new MockWebServer();
            try {
                backend.start();
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }

        registry.add(EUREKA_ENABLED_PROPERTY, () -> FALSE);
        registry.add(DISCOVERY_ENABLED_PROPERTY, () -> FALSE);
        registry.add(DISCOVERY_LOCATOR_ENABLED_PROPERTY, () -> FALSE);
        registry.add(ROUTE_ID_PROPERTY, () -> ROUTE_ID);
        registry.add(ROUTE_URI_PROPERTY, () -> backend.url(ROOT_PATH).toString());
        registry.add(ROUTE_PREDICATE_PROPERTY, () -> ROUTE_PREDICATE);
    }

    @Test
    void shouldRouteUserRequestToIdentityService() throws Exception {
        backend.enqueue(mockResponse(200, RESPONSE_OK));

        HttpRequest request = httpGetRequest(gatewayPort, PATH_USER_42);
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        org.assertj.core.api.Assertions.assertThat(response.statusCode()).isEqualTo(200);
        org.assertj.core.api.Assertions.assertThat(response.body()).isEqualTo(RESPONSE_OK);

        RecordedRequest routedRequest = backend.takeRequest(2, TimeUnit.SECONDS);
        org.assertj.core.api.Assertions.assertThat(routedRequest).isNotNull();
        org.assertj.core.api.Assertions.assertThat(routedRequest.getPath()).isEqualTo(PATH_USER_42);
        verifyNoInteractions(tokenValidationService);
    }

    @Test
    void shouldRouteAuthRequestAndInjectUserHeadersWhenAuthorizationProvided() throws Exception {
        backend.enqueue(mockResponse(200, RESPONSE_AUTHORIZED));
        String token = BEARER_TOKEN;
        UserResponse user = userResponse(5, USERNAME_ALICE, EMAIL_ALICE, USER_ROLE);
        when(tokenValidationService.getUserFromToken(token)).thenReturn(user);

        HttpRequest request = httpGetRequestWithHeader(gatewayPort, PATH_AUTH_LOGIN, AUTH_HEADER, token);
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        org.assertj.core.api.Assertions.assertThat(response.statusCode()).isEqualTo(200);
        org.assertj.core.api.Assertions.assertThat(response.body()).isEqualTo(RESPONSE_AUTHORIZED);

        RecordedRequest routedRequest = backend.takeRequest(2, TimeUnit.SECONDS);
        org.assertj.core.api.Assertions.assertThat(routedRequest).isNotNull();
        org.assertj.core.api.Assertions.assertThat(routedRequest.getHeader(USERNAME_HEADER)).isEqualTo(USERNAME_ALICE);
        org.assertj.core.api.Assertions.assertThat(routedRequest.getHeader(EMAIL_HEADER)).isEqualTo(EMAIL_ALICE);
        org.assertj.core.api.Assertions.assertThat(routedRequest.getHeader(ROLE_HEADER)).isEqualTo(USER_ROLE);
        verify(tokenValidationService).getUserFromToken(token);
    }

    @Test
    void shouldNotRouteUnmatchedPath() throws Exception {
        HttpRequest request = httpGetRequest(gatewayPort, PATH_UNMATCHED);
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        org.assertj.core.api.Assertions.assertThat(response.statusCode()).isEqualTo(404);
    }

    @TestConfiguration
    static class TestConfig {
        @Bean
        @Primary
        TokenValidationService tokenValidationService() {
            return Mockito.mock(TokenValidationService.class);
        }
    }
}
