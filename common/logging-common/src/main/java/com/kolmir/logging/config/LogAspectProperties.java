package com.kolmir.logging.config;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;


@ConfigurationProperties(prefix = "logging.aspect")
public record LogAspectProperties (
    List<String> include,
    List<String> exclude
){
    public LogAspectProperties {
        include = include == null ? List.of("* com.kolmir.*.service..*(..)") : include;
        exclude = exclude == null ? List.of() : exclude;
    }
}
