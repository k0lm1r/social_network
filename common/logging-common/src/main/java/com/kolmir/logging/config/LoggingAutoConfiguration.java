package com.kolmir.logging.config;

import org.aspectj.lang.annotation.Aspect;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import com.kolmir.logging.aop.LoggingAspect;
import com.kolmir.logging.matcher.LogAspectMatcher;
import com.kolmir.logging.sanitizer.LogSanitizer;

import lombok.RequiredArgsConstructor;


@AutoConfiguration
@RequiredArgsConstructor
@ConditionalOnClass(Aspect.class)
@EnableConfigurationProperties(LogAspectProperties.class)
public class LoggingAutoConfiguration {
    private final LogSanitizer logSanitizer;
    private final LogAspectMatcher logAspectMatcher;

    @Bean
    public LoggingAspect loggingAspect() {
        return new LoggingAspect(logSanitizer, logAspectMatcher);
    }
}
