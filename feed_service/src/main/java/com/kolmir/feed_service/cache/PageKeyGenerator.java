package com.kolmir.feed_service.cache;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.jspecify.annotations.Nullable;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;


@Component
public class PageKeyGenerator implements KeyGenerator {
    @Override
    public Object generate(Object target, Method method, @Nullable Object... params) {
        String keyParts = Arrays.stream(params)
            .map(this::toKeyPart)
            .collect(Collectors.joining("|"));

        return target.getClass().getSimpleName() + ":" + method.getName() + ":" + keyParts;
    }

    private String toKeyPart(Object param) {
        if (param instanceof Pageable p) {
            return "p=%d,s=%d,sort=%s".formatted(
                p.getPageNumber(),
                p.getPageSize(),
                sortToString(p.getSort())
            );
        }
        return String.valueOf(param);
    }

    private String sortToString(Sort sort) {
        return sort.stream()
            .map(o -> o.getProperty() + "," + o.getDirection())
            .collect(Collectors.joining(";"));
    }
}
