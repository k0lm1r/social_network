package com.kolmir.identity_service.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import com.kolmir.identity_service.dto.UserAuthResponse;
import com.kolmir.identity_service.dto.UserRegisterResponse;
import static com.kolmir.identity_service.util.AuthConstants.*;

import lombok.extern.slf4j.Slf4j;


@Slf4j
@Aspect
@Component
public class LoggingAspect {

    @Pointcut("within(com.kolmir.identity_service.service..*)")
    public void serviceLayer() {}

    @Pointcut("!within(com.kolmir.identity_service.service.UserAuthProviderImpl)")
    public void userAuthProviderExclude() {}

    @Around("serviceLayer() && userAuthProviderExclude()")
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
                log.info("result - {}, {} ms", protectResult(result), end - start);
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

    private Object protectResult(Object result) {
        if (result instanceof UserAuthResponse) {
           return getSafeAuthResponse((UserAuthResponse)result);
        } else if (result instanceof UserRegisterResponse) {
            UserRegisterResponse response = (UserRegisterResponse) result;
            return new UserRegisterResponse(getSafeAuthResponse(response.auth()), response.user());
        }
        return result;
    }

    private UserAuthResponse getSafeAuthResponse(UserAuthResponse response) {
        return new UserAuthResponse(
                    REDACTED_TOKEN, 
                    response.accessExpiresIn(), 
                    REDACTED_TOKEN, response.refreshExpiresIn()
                );
    }
}
