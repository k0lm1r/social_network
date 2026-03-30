package com.kolmir.identity_service.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Aspect
@Component
public class LoggingAspect {
    @Around("execution(* com.kolmir.identity_service.service..*.*(..))")
    public Object logMethod (ProceedingJoinPoint jp) throws Throwable {
        String methodName = jp.getSignature().toShortString();
        Object[] args = jp.getArgs();

        log.info("method - {}, args - {}", methodName, args);

        long start = System.currentTimeMillis();

        try {
            Object result = jp.proceed();
            long end = System.currentTimeMillis();

            if (result == null) {
                log.info("result - null, {} ms", end - start);
            } else {
                log.info("result - {}, {} ms", result, end - start);
            }

            return result;
        } catch (RuntimeException e) {
            log.warn("runtime exception - {}", e.getMessage());
            throw e;
        } catch(Throwable t) {
            log.error("exception - {}", t.getMessage());
            throw t;
        }
    }
}
