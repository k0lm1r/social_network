package com.kolmir.api_gateway;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.URI;
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

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GatewayIntegrationTest {

    private static final String AUTH_HEADER = "Authorization";
    private static final String USERNAME_HEADER = "X-User-Name";
    private static final String EMAIL_HEADER = "X-User-Email";
    private static final String ROLE_HEADER = "X-User-Role";

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

        registry.add("eureka.client.enabled", () -> "false");
        registry.add("spring.cloud.discovery.enabled", () -> "false");
        registry.add("spring.cloud.gateway.server.webflux.discovery.locator.enabled", () -> "false");
        registry.add("spring.cloud.gateway.server.webflux.routes[0].id", () -> "identity-route-test");
        registry.add("spring.cloud.gateway.server.webflux.routes[0].uri", () -> backend.url("/").toString());
        registry.add("spring.cloud.gateway.server.webflux.routes[0].predicates[0]", () -> "Path=/api/users/**,/api/auth/**");
    }

    @Test
    void shouldRouteUserRequestToIdentityService() throws Exception {
        backend.enqueue(new MockResponse().setResponseCode(200).setBody("ok"));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + gatewayPort + "/api/users/42"))
                .GET()
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        org.assertj.core.api.Assertions.assertThat(response.statusCode()).isEqualTo(200);
        org.assertj.core.api.Assertions.assertThat(response.body()).isEqualTo("ok");

        RecordedRequest routedRequest = backend.takeRequest(2, TimeUnit.SECONDS);
        org.assertj.core.api.Assertions.assertThat(routedRequest).isNotNull();
        org.assertj.core.api.Assertions.assertThat(routedRequest.getPath()).isEqualTo("/api/users/42");
        verifyNoInteractions(tokenValidationService);
    }

    @Test
    void shouldRouteAuthRequestAndInjectUserHeadersWhenAuthorizationProvided() throws Exception {
        backend.enqueue(new MockResponse().setResponseCode(200).setBody("authorized"));
        String token = "Bearer token-value";
        UserResponse user = UserResponse.newBuilder()
                .setId(5)
                .setUsername("alice")
                .setEmail("alice@test.com")
                .setRole("USER")
                .build();
        when(tokenValidationService.getUserFromToken(token)).thenReturn(user);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + gatewayPort + "/api/auth/login"))
                .header(AUTH_HEADER, token)
                .GET()
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        org.assertj.core.api.Assertions.assertThat(response.statusCode()).isEqualTo(200);
        org.assertj.core.api.Assertions.assertThat(response.body()).isEqualTo("authorized");

        RecordedRequest routedRequest = backend.takeRequest(2, TimeUnit.SECONDS);
        org.assertj.core.api.Assertions.assertThat(routedRequest).isNotNull();
        org.assertj.core.api.Assertions.assertThat(routedRequest.getHeader(USERNAME_HEADER)).isEqualTo("alice");
        org.assertj.core.api.Assertions.assertThat(routedRequest.getHeader(EMAIL_HEADER)).isEqualTo("alice@test.com");
        org.assertj.core.api.Assertions.assertThat(routedRequest.getHeader(ROLE_HEADER)).isEqualTo("USER");
        verify(tokenValidationService).getUserFromToken(token);
    }

    @Test
    void shouldNotRouteUnmatchedPath() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + gatewayPort + "/unmatched/path"))
                .GET()
                .build();
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
