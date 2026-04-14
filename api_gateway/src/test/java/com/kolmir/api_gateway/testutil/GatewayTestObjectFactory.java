package com.kolmir.api_gateway.testutil;

import com.kolmir.validate_token.UserResponse;

import lombok.experimental.UtilityClass;

import java.net.URI;
import java.net.http.HttpRequest;
import okhttp3.mockwebserver.MockResponse;


@UtilityClass
public class GatewayTestObjectFactory {
    public static HttpRequest httpGetRequest(int gatewayPort, String path) {
        return HttpRequest.newBuilder()
                .uri(URI.create(GatewayTestConstants.BASE_URL_PREFIX + gatewayPort + path))
                .GET()
                .build();
    }

    public static HttpRequest httpGetRequestWithHeader(
            int gatewayPort,
            String path,
            String headerName,
            String headerValue
    ) {
        return HttpRequest.newBuilder()
                .uri(URI.create(GatewayTestConstants.BASE_URL_PREFIX + gatewayPort + path))
                .header(headerName, headerValue)
                .GET()
                .build();
    }

    public static MockResponse mockResponse(int responseCode, String body) {
        return new MockResponse().setResponseCode(responseCode).setBody(body);
    }

    public static UserResponse userResponse(int id, String username, String email, String role) {
        return UserResponse.newBuilder()
                .setId(id)
                .setUsername(username)
                .setEmail(email)
                .setRole(role)
                .build();
    }
}
