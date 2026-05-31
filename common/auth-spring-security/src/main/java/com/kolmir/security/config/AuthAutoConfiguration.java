package com.kolmir.security.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.context.SecurityContextHolder;

import com.kolmir.security.filter.UserHeadersFilter;
import com.kolmir.security.provider.CurrentUserProvider;


@AutoConfiguration
@ConditionalOnClass(SecurityContextHolder.class)
public class AuthAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean
    public UserHeadersFilter userHeadersFilter() {
        return new UserHeadersFilter();
    }

    @Bean
    @ConditionalOnMissingBean
    public CurrentUserProvider currentUserProvider() {
        return new CurrentUserProvider();
    }
}
