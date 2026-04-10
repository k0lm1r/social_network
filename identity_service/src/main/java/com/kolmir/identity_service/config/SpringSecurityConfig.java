package com.kolmir.identity_service.config;

import com.kolmir.identity_service.model.UserRole;
import com.kolmir.identity_service.security.KeycloakAuthoritiesConverter;
import static com.kolmir.identity_service.util.UserConstants.*;
import static com.kolmir.identity_service.util.AuthConstants.AUTH_PATH;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
public class SpringSecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    AUTH_PATH + "/**",
                    "/actuator/**",
                    "/v3/api-docs/**",
                    "/swagger-ui/**",
                    "/swagger-ui.html"
                ).permitAll()
                .requestMatchers(USER_DISABLE_URL, USER_MAIN_URL).hasRole(UserRole.ADMIN.getStringName())
                .anyRequest().authenticated()
            )
            .sessionManagement(
                session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.jwtAuthenticationConverter(new KeycloakAuthoritiesConverter()))
            );
            
        return http.build();
    }
}
