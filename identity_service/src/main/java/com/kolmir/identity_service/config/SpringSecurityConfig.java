package com.kolmir.identity_service.config;

import com.kolmir.identity_service.model.UserRole;
import com.kolmir.identity_service.security.KeycloakAuthoritiesConverter;
import static com.kolmir.identity_service.util.IdentityServiceConstants.*;
import static com.kolmir.identity_service.util.UserUtils.*;
import static com.kolmir.identity_service.util.AuthUtils.AUTH_PATH;
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
                    USER_MAIN_URL + USER_ID_URL,
                    ACTUATOR_URL,
                    API_DOCS_URL,
                    SWAGGER_UI_URL,
                    SWAGGER_UI_HTML_URL
                ).permitAll()
                .requestMatchers(CHANGE_ROLE_URL).hasRole(UserRole.MAIN_ADMIN.getStringName())
                .requestMatchers(USER_DISABLE_URL, USER_MAIN_URL).hasAnyRole(
                    UserRole.ADMIN.getStringName(), 
                    UserRole.MAIN_ADMIN.getStringName()
                ).anyRequest().authenticated()
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
