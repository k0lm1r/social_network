package com.kolmir.subscription_service.openfeign.service.impl;

import static com.kolmir.subscription_service.util.SubscriptionServiceConstants.USER_ID_WAS_NOT_VALIDATED_EXCEPTION;

import org.springframework.stereotype.Service;

import com.kolmir.subscription_service.exception.ExternalServiceException;
import com.kolmir.subscription_service.openfeign.UserClient;
import com.kolmir.subscription_service.openfeign.service.UserExistenceService;

import feign.FeignException;
import feign.RetryableException;
import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class UserExistenceServiceImpl implements UserExistenceService {
    private final UserClient userClient;

    @Override
    public boolean isUserExists(Long userId) {
        try {
            userClient.getUserById(userId);
            return true;
        } catch (RetryableException e) {
            throw new ExternalServiceException(USER_ID_WAS_NOT_VALIDATED_EXCEPTION);
        } catch (FeignException _) {
            return false;
        }
    }
}
