package com.kolmir.subscription_service.openfeign.service;


public interface UserExistenceService {
    public void validateUserExists(Long userId);
}
