package com.kolmir.logging.sanitizer;

import static com.kolmir.logging.util.AuthUtils.*;

import java.lang.reflect.Array;
import java.lang.reflect.RecordComponent;
import java.util.*;


public class LogSanitizer {
    public Object mask(Object valueForMasking) {
        return mask(valueForMasking, new IdentityHashMap<>());
    }

    private Object mask(Object valueForMasking, IdentityHashMap<Object, Boolean> visited) {
        if (valueForMasking == null) return null;
        if (isPrimitiveLike(valueForMasking)) return maskStringIfNeeded(valueForMasking);

        if (visited.put(valueForMasking, Boolean.TRUE) != null) return CYCLE_MESSAGE;

        if (valueForMasking instanceof Map<?, ?> map) {
            Map<Object, Object> maskedMap = new LinkedHashMap<>();
            for (var entrySet : map.entrySet()) {
                Object key = entrySet.getKey();
                Object value = entrySet.getValue();
                if (key instanceof String s && isSensitiveKey(s)) maskedMap.put(key, REDACTED_MESSAGE);
                else maskedMap.put(key, mask(value, visited));
            }
            return maskedMap;
        } else if (valueForMasking instanceof Collection<?> c) {
            return c.stream().map(v -> mask(v, visited)).toList();
        } else if (valueForMasking.getClass().isArray()) {
            int len = Array.getLength(valueForMasking);
            Object[] masked = new Object[len];
            for (int i = 0; i < len; i++) {
                masked[i] = mask(Array.get(valueForMasking, i), visited);
            }
            return masked;
        }

        Class<?> type = valueForMasking.getClass();
        if (type.isRecord()) return maskRecord(valueForMasking, visited);

        return valueForMasking;
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

    private Object maskStringIfNeeded(Object str) {
        if (str instanceof String s && BEARER.matcher(s).matches()) return REDACTED_MESSAGE;
        return str;
    }

    private boolean isSensitiveKey(String key) {
        return SENSITIVE_KEYS.stream().anyMatch(k -> k.equalsIgnoreCase(key));
    }

    private boolean isPrimitiveLike(Object primitive) {
        return primitive instanceof String || primitive instanceof Number || primitive instanceof Boolean || primitive.getClass().isEnum();
    }
}
