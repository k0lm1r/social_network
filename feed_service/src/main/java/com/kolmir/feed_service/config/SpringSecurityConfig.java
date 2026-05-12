package com.kolmir.feed_service.config;

import static com.kolmir.feed_service.util.CommentUtil.*;
import static com.kolmir.feed_service.util.PostUtil.*;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;

import com.kolmir.security.filter.UserHeadersFilter;

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
                .requestMatchers(POST_MAIN_URL + FEED_URL).authenticated()
                .requestMatchers(HttpMethod.GET, POST_MAIN_URL + "/**", COMMENT_MAIN_URL + "/**").permitAll()
                .requestMatchers(HttpMethod.PATCH, POST_MAIN_URL + POPULARITY_URL).permitAll()
                .anyRequest().authenticated()
            )
            .sessionManagement(
                session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            );
            
        return http.build();
    }
}
