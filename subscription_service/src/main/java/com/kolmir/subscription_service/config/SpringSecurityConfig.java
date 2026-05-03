package com.kolmir.subscription_service.config;

import static com.kolmir.subscription_service.util.InteractionEventUtil.*;
import static com.kolmir.subscription_service.util.ReactionUtil.*;
import static com.kolmir.subscription_service.util.SubscriptionLinkUtil.*;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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
                .requestMatchers(HttpMethod.POST, SUBSCRIPTION_MAIN_URL + FOLLOW_URL, POST_MAIN_URL + POST_ID_URL).authenticated()
                .requestMatchers(HttpMethod.DELETE, SUBSCRIPTION_MAIN_URL + UNFOLLOW_URL).authenticated()
                .requestMatchers(HttpMethod.PATCH, POST_MAIN_URL + POST_ID_URL).authenticated()
                .requestMatchers(
                    SUBSCRIPTION_MAIN_URL + "**",
                    POST_MAIN_URL + "**"
                ).permitAll()
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
