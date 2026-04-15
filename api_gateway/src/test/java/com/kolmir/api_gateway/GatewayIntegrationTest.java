package com.kolmir.api_gateway;

import static com.kolmir.api_gateway.testutil.ApiGatewayTestConstants.AUTH_HEADER;
import static com.kolmir.api_gateway.testutil.ApiGatewayTestConstants.DISCOVERY_ENABLED_PROPERTY;
import static com.kolmir.api_gateway.testutil.ApiGatewayTestConstants.DISCOVERY_LOCATOR_ENABLED_PROPERTY;
import static com.kolmir.api_gateway.testutil.ApiGatewayTestConstants.EMAIL_ALICE;
import static com.kolmir.api_gateway.testutil.ApiGatewayTestConstants.EMAIL_HEADER;
import static com.kolmir.api_gateway.testutil.ApiGatewayTestConstants.EUREKA_ENABLED_PROPERTY;
import static com.kolmir.api_gateway.testutil.ApiGatewayTestConstants.FALSE;
import static com.kolmir.api_gateway.testutil.ApiGatewayTestConstants.PATH_AUTH_LOGIN;
import static com.kolmir.api_gateway.testutil.ApiGatewayTestConstants.PATH_UNMATCHED;
import static com.kolmir.api_gateway.testutil.ApiGatewayTestConstants.PATH_USER_42;
import static com.kolmir.api_gateway.testutil.ApiGatewayTestConstants.RESPONSE_AUTHORIZED;
import static com.kolmir.api_gateway.testutil.ApiGatewayTestConstants.RESPONSE_OK;
import static com.kolmir.api_gateway.testutil.ApiGatewayTestConstants.ROLE_HEADER;
import static com.kolmir.api_gateway.testutil.ApiGatewayTestConstants.ROOT_PATH;
import static com.kolmir.api_gateway.testutil.ApiGatewayTestConstants.SIMPLE_DISCOVERY_URI_PROPERTY;
import static com.kolmir.api_gateway.testutil.ApiGatewayTestConstants.REQUEST_POLL_MILLIS;
import static com.kolmir.api_gateway.testutil.ApiGatewayTestConstants.REQUEST_TIMEOUT_SECONDS;
import static com.kolmir.api_gateway.testutil.ApiGatewayTestConstants.HTTP_OK;
import static com.kolmir.api_gateway.testutil.ApiGatewayTestConstants.HTTP_NOT_FOUND;
import static com.kolmir.api_gateway.testutil.ApiGatewayTestConstants.USER_ID_ALICE;
import static com.kolmir.api_gateway.testutil.ApiGatewayTestConstants.USERNAME_ALICE;
import static com.kolmir.api_gateway.testutil.ApiGatewayTestConstants.USERNAME_HEADER;
import static com.kolmir.api_gateway.testutil.ApiGatewayTestConstants.USER_ROLE;
import static com.kolmir.api_gateway.testutil.ApiGatewayTestConstants.BEARER_TOKEN;
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
        while (backend.takeRequest(REQUEST_POLL_MILLIS, TimeUnit.MILLISECONDS) != null) {
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
        registry.add(DISCOVERY_ENABLED_PROPERTY, () -> "true");
        registry.add(DISCOVERY_LOCATOR_ENABLED_PROPERTY, () -> FALSE);
        registry.add(SIMPLE_DISCOVERY_URI_PROPERTY, () -> backend.url(ROOT_PATH).toString());
    }

    @Test
    void shouldRouteUserRequestToIdentityService() throws Exception {
        backend.enqueue(mockResponse(HTTP_OK, RESPONSE_OK));

        HttpRequest request = httpGetRequest(gatewayPort, PATH_USER_42);
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        org.assertj.core.api.Assertions.assertThat(response.statusCode()).isEqualTo(HTTP_OK);
        org.assertj.core.api.Assertions.assertThat(response.body()).isEqualTo(RESPONSE_OK);

        RecordedRequest routedRequest = backend.takeRequest(REQUEST_TIMEOUT_SECONDS, TimeUnit.SECONDS);
        org.assertj.core.api.Assertions.assertThat(routedRequest).isNotNull();
        org.assertj.core.api.Assertions.assertThat(routedRequest.getPath()).isEqualTo(PATH_USER_42);
        verifyNoInteractions(tokenValidationService);
    }

    @Test
    void shouldRouteAuthRequestAndInjectUserHeadersWhenAuthorizationProvided() throws Exception {
        backend.enqueue(mockResponse(HTTP_OK, RESPONSE_AUTHORIZED));
        String token = BEARER_TOKEN;
        UserResponse user = userResponse(USER_ID_ALICE, USERNAME_ALICE, EMAIL_ALICE, USER_ROLE);
        when(tokenValidationService.getUserFromToken(token)).thenReturn(user);

        HttpRequest request = httpGetRequestWithHeader(gatewayPort, PATH_AUTH_LOGIN, AUTH_HEADER, token);
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        org.assertj.core.api.Assertions.assertThat(response.statusCode()).isEqualTo(HTTP_OK);
        org.assertj.core.api.Assertions.assertThat(response.body()).isEqualTo(RESPONSE_AUTHORIZED);

        RecordedRequest routedRequest = backend.takeRequest(REQUEST_TIMEOUT_SECONDS, TimeUnit.SECONDS);
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

        org.assertj.core.api.Assertions.assertThat(response.statusCode()).isEqualTo(HTTP_NOT_FOUND);
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
