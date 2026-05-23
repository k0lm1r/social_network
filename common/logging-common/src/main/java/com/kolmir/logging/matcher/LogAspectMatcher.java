package com.kolmir.logging.matcher;

import java.util.List;

import org.springframework.util.PatternMatchUtils;

import com.kolmir.logging.config.LogAspectProperties;

import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
public class LogAspectMatcher {
    private final LogAspectProperties properties;

    public boolean isIncluded(String methodName) {
        return isMatch(properties.include(), methodName);
    }

    public boolean isExcluded(String methodName) {
        return isMatch(properties.exclude(), methodName);
    }

    private boolean isMatch(List<String> patterns, String str) {
        return patterns.stream()
            .anyMatch(
                pattern -> PatternMatchUtils.simpleMatch(pattern, str)
            );
    }
}
