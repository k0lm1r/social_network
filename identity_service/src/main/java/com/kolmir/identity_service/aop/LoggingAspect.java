package com.kolmir.identity_service.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import com.kolmir.identity_service.dto.auth.RefreshTokenRequest;
import com.kolmir.identity_service.dto.auth.UserAuthRequest;
import com.kolmir.identity_service.dto.auth.UserAuthResponse;
import com.kolmir.identity_service.dto.auth.UserRegisterRequest;
import com.kolmir.identity_service.dto.auth.UserRegisterResponse;

import static com.kolmir.identity_service.util.AuthUtils.*;

import java.util.Arrays;

import lombok.extern.slf4j.Slf4j;


@Slf4j
@Aspect
@Component
public class LoggingAspect {

    @Pointcut("execution(* com.kolmir.identity_service.service..*(..))")
    public void serviceLayer() {}

    @Pointcut("!execution(* com.kolmir.identity_service.service.impl.UserAuthProviderImpl.*(..))")
    public void userAuthProviderExclude() {}

    @Around("serviceLayer() && userAuthProviderExclude()")
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
                log.info("result - {}, {} ms", protectData(result), end - start);
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
                .map(this::protectData)
                .toArray();
    }

    private Object protectData(Object result) {
        return switch (result) {
            case UserAuthResponse r -> getSafeAuthResponse(r);
            case UserRegisterResponse r -> getSafeRegisterResponse(r);
            case RefreshTokenRequest r -> getSafeRefreshTokenRequest(r);
            case UserRegisterRequest r -> getSafeRegisterRequest(r);
            case UserAuthRequest r -> getSafeAuthRequest(r);
            default -> result;
        };
    }
}
