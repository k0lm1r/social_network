package com.kolmir.subscription_service.config;

import static com.kolmir.subscription_service.util.InteractionEventUtil.EVENT_MAIN_URL;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;

import com.kolmir.subscription_service.security.UserHeadersFilter;
import static com.kolmir.subscription_service.util.SubscriptionServiceConstants.*;

import lombok.RequiredArgsConstructor;


@Configuration
@RequiredArgsConstructor
public class SpringSecurityConfig {
    private final UserHeadersFilter userHeadersFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) {
        http
            .addFilterBefore(userHeadersFilter, AnonymousAuthenticationFilter.class)
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(EVENT_MAIN_URL + "/**").hasAnyRole(
                    ADMIN_ROLE,
                    MAIN_ADMIN_ROLE
                ).anyRequest().authenticated()
            )
            .sessionManagement(
                session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            );
            
        return http.build();
    }
}
