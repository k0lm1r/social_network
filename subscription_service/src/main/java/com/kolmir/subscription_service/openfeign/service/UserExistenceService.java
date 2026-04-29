package com.kolmir.subscription_service.openfeign.service;


public interface UserExistenceService {
    public boolean isUserExists(Long userId);
}
