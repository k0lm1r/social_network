package com.kolmir.logging.sanitizer;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static com.kolmir.logging.util.AuthUtils.REDACTED_MESSAGE;


@Target({ElementType.FIELD, ElementType.RECORD_COMPONENT})
@Retention(RetentionPolicy.RUNTIME)
public @interface Sensitive {
    String mask() default REDACTED_MESSAGE;
}
