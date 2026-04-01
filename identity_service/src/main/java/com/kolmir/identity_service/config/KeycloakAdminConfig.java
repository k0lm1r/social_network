package com.kolmir.identity_service.config;

import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class KeycloakAdminConfig {
    @Value("${keycloak.server-url}") 
    public String serverUrl;

    @Value("${keycloak.realm}") 
    public String realm;

    @Value("${keycloak.client-id}")
    public String clientId;

    @Value("${keycloak.client-secret}")
    public String clientSecret;
    
    @Value("${keycloak.username}")
    private String username;
    
    @Value("${keycloak.password}") 
    private String password;

    @Bean
    public Keycloak keycloak() {
        return KeycloakBuilder.builder()
                .serverUrl(serverUrl)
                .realm(realm)
                .clientId(clientId)
                .clientSecret(clientSecret)
                .grantType(OAuth2Constants.PASSWORD)
                .username(username)
                .password(password)
                .build();
    }
}
