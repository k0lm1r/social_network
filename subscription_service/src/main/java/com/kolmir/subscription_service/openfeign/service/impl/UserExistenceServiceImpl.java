package com.kolmir.subscription_service.openfeign.service.impl;

import static com.kolmir.subscription_service.util.SubscriptionServiceConstants.USER_ID_WAS_NOT_VALIDATED_EXCEPTION;
import static com.kolmir.subscription_service.util.SubscriptionServiceConstants.USER_NOT_EXISTS_MESSAGE;

import org.springframework.stereotype.Service;

import com.kolmir.subscription_service.exception.ExternalServiceException;
import com.kolmir.subscription_service.exception.NotFoundException;
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
    public void validateUserExists(Long userId) {
        try {
            if (!userClient.isUserExistsById(userId))
                throw new NotFoundException(USER_NOT_EXISTS_MESSAGE);
        } catch (RetryableException _) {
            throw new ExternalServiceException(USER_ID_WAS_NOT_VALIDATED_EXCEPTION);
        } catch (FeignException e) {
            throw new ExternalServiceException(e.getMessage());
        }
    }
}
