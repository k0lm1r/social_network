package com.kolmir.identity_service.config;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
public class SpringSecurityConfig {

    @Value("${keycloak-clientid}")
    private String clientId;

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("api/users/{id}").permitAll()
                .requestMatchers("api/users/disable").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .sessionManagement(
                session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
            );
            
        return http.build();
    }

    @Bean
    Converter<Jwt, ? extends AbstractAuthenticationToken> jwtAuthenticationConverter() {
        return jwt -> {
            Collection<SimpleGrantedAuthority> authorities = extractAuthorities(jwt);
            String principalName = jwt.getClaimAsString("preferred_username");
            return new JwtAuthenticationToken(jwt, authorities, principalName);
        };
    }

    private List<SimpleGrantedAuthority> extractAuthorities(Jwt jwt) {
        Map<String, Object> realmAccess = jwt.getClaim("realm_access");
        List<String> realmRoles = realmAccess == null
                ? List.of()
                : (List<String>) realmAccess.getOrDefault("roles", List.of());

        Map<String, Object> resourceAccess = jwt.getClaim("resource_access");
        List<String> clientRoles = List.of();

        if (resourceAccess != null && resourceAccess.containsKey(clientId)) {
            Map<String, Object> client = (Map<String, Object>) resourceAccess.get(clientId);
            clientRoles = (List<String>) client.getOrDefault("roles", List.of());
        }

        return Stream.concat(
                realmRoles.stream().map(r -> new SimpleGrantedAuthority("ROLE_" + r)),
                clientRoles.stream().map(r -> new SimpleGrantedAuthority("ROLE_" + r))
        ).distinct().toList();
    }
}
