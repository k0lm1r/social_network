package com.kolmir.api_gateway.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import static com.kolmir.api_gateway.util.FilterConstants.*;

import java.util.Arrays;

import lombok.extern.slf4j.Slf4j;


@Slf4j
@Aspect
@Component
public class LoggingAspect {
    @Around("execution(* com.kolmir.api_gateway.service..*.*(..))")
    public Object logMethod (ProceedingJoinPoint jp) throws Throwable {
        String methodName = jp.getSignature().toShortString();
        Object[] args = jp.getArgs();

        log.info("method - {}, args - {}", methodName, protectArgs(args));

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
            log.warn("runtime exception - {}", e);
            throw e;
        } catch(Throwable t) {
            log.error("exception - {}", t);
            throw t;
        }
    }

    private Object[] protectArgs(Object[] args) {
        return Arrays.stream(args)
                    .map(this::protectArg)
                    .toArray();
    }

    private Object protectArg(Object arg) {
        if (arg instanceof String && ((String)arg).startsWith("Bearer "))
            return REDACTERED_TOKEN;
        return arg;
    }
}
