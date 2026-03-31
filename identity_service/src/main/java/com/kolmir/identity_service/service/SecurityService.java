package com.kolmir.identity_service.service;

public interface SecurityService {
    public boolean isCurrentUserOwner(Long id);
}
