package com.kolmir.logging.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import com.kolmir.logging.matcher.LogAspectMatcher;
import com.kolmir.logging.sanitizer.LogSanitizer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Aspect
@RequiredArgsConstructor
public class LoggingAspect {

    private final LogSanitizer logSanitizer;
    private final LogAspectMatcher logAspectMatcher;

    @Pointcut("execution(* com.kolmir.*.service..*(..))")
    public void serviceLayer() {}

    @Around("serviceLayer()")
    public Object logMethod (ProceedingJoinPoint jp) throws Throwable {
        String fullName = jp.getSignature().toLongString();

        if (!logAspectMatcher.isIncluded(fullName) || logAspectMatcher.isExcluded(fullName)) {
            return jp.proceed();
        }
        
        String methodName = jp.getSignature().toShortString();
        Object[] args = jp.getArgs();

        log.info("method - {}, args - {}", methodName, logSanitizer.mask(args));

        long start = System.currentTimeMillis();

        try {
            Object result = jp.proceed();
            long end = System.currentTimeMillis();

            if (result == null) {
                log.info("result - null, {} ms", end - start);
            } else {
                log.info("result - {}, {} ms", logSanitizer.mask(result), end - start);
            }

            return result;
        } catch (RuntimeException e) {
            log.warn("runtime exception - {}", e);
            throw e;
        } catch(Throwable t) {
            log.error("exception - {}", t);
            throw t;
        }
    }
}
