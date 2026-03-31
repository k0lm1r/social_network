package com.kolmir.identity_service.config;

import com.kolmir.identity_service.model.UserRole;
import com.kolmir.identity_service.security.KeycloakAuthoritiesConverter;
import static com.kolmir.identity_service.util.UserConstants.*;
import static com.kolmir.identity_service.util.KeycloakConstants.*;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
public class SpringSecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(USER_MAIN_URL).permitAll()
                .requestMatchers(USER_DISABLE_URL).hasRole(UserRole.ADMIN.getStringName())
                .anyRequest().authenticated()
            )
            .sessionManagement(
                session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.jwtAuthenticationConverter(keycloakAuthConverter()))
            );
            
        return http.build();
    }

    @Bean
    public Converter<Jwt, ? extends AbstractAuthenticationToken> keycloakAuthConverter() {
        var converter = new JwtAuthenticationConverter();
        converter.setPrincipalClaimName(CLAIM_NAME);
        converter.setJwtGrantedAuthoritiesConverter(new KeycloakAuthoritiesConverter());
        return converter;
    }
}
