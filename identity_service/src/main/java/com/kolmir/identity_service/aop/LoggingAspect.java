package com.kolmir.identity_service.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import com.kolmir.identity_service.logging.LogSanitizer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class LoggingAspect {

    private final LogSanitizer logSanitizer;

    @Pointcut("execution(* com.kolmir.identity_service.service..*(..))")
    public void serviceLayer() {}

    @Pointcut("!execution(* com.kolmir.identity_service.service.impl.UserAuthProviderImpl.*(..))")
    public void userAuthProviderExclude() {}

    @Around("serviceLayer() && userAuthProviderExclude()")
    public Object logMethod (ProceedingJoinPoint jp) throws Throwable {
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
