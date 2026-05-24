package com.kolmir.logging.matcher;

import java.util.List;
import java.util.regex.Pattern;

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
            .anyMatch(pattern -> isPatternMatch(pattern, str));
    }

    private boolean isPatternMatch(String pattern, String target) {
        if (pattern == null || pattern.isBlank())
            return false;

        if (!pattern.contains(".."))
            return PatternMatchUtils.simpleMatch(pattern, target);

        return Pattern.compile(toRegex(pattern))
            .matcher(target)
            .matches();
    }

    private String toRegex(String pattern) {
        StringBuilder regex = new StringBuilder("^");

        for (int i = 0; i < pattern.length(); i++) {
            char ch = pattern.charAt(i);

            if (ch == '.' && i + 1 < pattern.length() && pattern.charAt(i + 1) == '.' || ch == '*') {
                regex.append(".*");
                i++;
                continue;
            }

            if ("\\.^$|?+()[]{}".indexOf(ch) >= 0)
                regex.append("\\");
            regex.append(ch);
        }

        regex.append("$");
        return regex.toString();
    }
}
