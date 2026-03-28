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
    String serverUrl;

    @Value("${keycloak.realm}") 
    String realm;

    @Value("${keycloak.client-id}")
    String clientId;

    @Value("${keycloak.username}")
    String username;
    
    @Value("${keycloak.password}") 
    String password;

    @Bean
    Keycloak keycloak() {
        return KeycloakBuilder.builder()
                .serverUrl(serverUrl)
                .realm(realm)
                .clientId(clientId)
                .grantType(OAuth2Constants.PASSWORD)
                .username(username)
                .password(password)
                .build();
    }
}
