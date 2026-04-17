package com.kolmir.identity_service.logging;

import org.springframework.stereotype.Component;

import static com.kolmir.identity_service.util.AuthUtils.*;

import java.lang.reflect.RecordComponent;
import java.util.*;


@Component
public class LogSanitizer {
    public Object mask(Object value) {
        return mask(value, new IdentityHashMap<>());
    }

    private Object mask(Object value, IdentityHashMap<Object, Boolean> visited) {
        if (value == null) return null;
        if (isPrimitiveLike(value)) return maskStringIfNeeded(value);

        if (visited.put(value, Boolean.TRUE) != null) return CYCLE_MESSAGE;

        if (value instanceof Map<?, ?> map) {
            Map<Object, Object> out = new LinkedHashMap<>();
            for (var e : map.entrySet()) {
                Object k = e.getKey();
                Object v = e.getValue();
                if (k instanceof String s && isSensitiveKey(s)) out.put(k, REDACTED_MESSAGE);
                else out.put(k, mask(v, visited));
            }
            return out;
        } else if (value instanceof Collection<?> c) {
            return c.stream().map(v -> mask(v, visited)).toList();
        } else if (value.getClass().isArray()) {
            Object[] arr = (Object[])value;
            return Arrays.stream(arr).map(v -> mask(v, visited)).toArray();
        }

        Class<?> type = value.getClass();
        if (type.isRecord()) return maskRecord(value, visited);

        return value;
    }

    private Object maskRecord(Object record, IdentityHashMap<Object, Boolean> visited) {
        try {
            Class<?> type = record.getClass();
            RecordComponent[] components = type.getRecordComponents();
            Object[] args = new Object[components.length];

            for (int i = 0; i < components.length; i++) {
                var c = components[i];
                Object v = c.getAccessor().invoke(record);
                boolean annotated = c.isAnnotationPresent(Sensitive.class);
                args[i] = annotated ? REDACTED_MESSAGE : mask(v, visited);
            }

            Class<?>[] sig = Arrays.stream(components).map(RecordComponent::getType).toArray(Class[]::new);
            return type.getDeclaredConstructor(sig).newInstance(args);
        } catch (Exception e) {
            return REDACTED_MESSAGE;
        }
    }

    private Object maskStringIfNeeded(Object v) {
        if (v instanceof String s && BEARER.matcher(s).matches()) return REDACTED_MESSAGE;
        return v;
    }

    private boolean isSensitiveKey(String key) {
        return SENSITIVE_KEYS.stream().anyMatch(k -> k.equalsIgnoreCase(key));
    }

    private boolean isPrimitiveLike(Object v) {
        return v instanceof String || v instanceof Number || v instanceof Boolean || v.getClass().isEnum();
    }
}
