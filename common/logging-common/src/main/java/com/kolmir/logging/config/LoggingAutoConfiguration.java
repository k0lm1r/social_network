package com.kolmir.logging.config;

import org.aspectj.lang.annotation.Aspect;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import com.kolmir.logging.aop.LoggingAspect;
import com.kolmir.logging.matcher.LogAspectMatcher;
import com.kolmir.logging.sanitizer.LogSanitizer;


@AutoConfiguration
@ConditionalOnClass(Aspect.class)
@EnableConfigurationProperties(LogAspectProperties.class)
public class LoggingAutoConfiguration {
    @Bean
    public LogAspectMatcher logAspectMatcher(LogAspectProperties properties) {
        return new LogAspectMatcher(properties);
    }

    @Bean
    public LoggingAspect loggingAspect(LogSanitizer logSanitizer, LogAspectMatcher logAspectMatcher) {
        return new LoggingAspect(logSanitizer, logAspectMatcher);
    }

    @Bean 
    public LogSanitizer logSanitizer() {
        return new LogSanitizer();
    }
}
