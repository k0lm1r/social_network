package com.kolmir.identity_service.logging;

import static com.kolmir.identity_service.util.AuthUtils.REDACTED_MESSAGE;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Target({ElementType.FIELD, ElementType.RECORD_COMPONENT})
@Retention(RetentionPolicy.RUNTIME)
public @interface Sensitive {
    String mask() default REDACTED_MESSAGE;
}
