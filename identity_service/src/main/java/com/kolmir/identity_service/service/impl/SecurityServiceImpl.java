package com.kolmir.identity_service.service.impl;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import com.kolmir.identity_service.repository.UserRepository;
import com.kolmir.identity_service.service.SecurityService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class SecurityServiceImpl implements SecurityService {

    private final UserRepository userRepository;

    @Override
    public boolean isCurrentUserOwner(Long id) {
        Jwt jwt = (Jwt) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        String keycloakId = jwt.getSubject();
        return userRepository.existsByIdAndKeycloakId(id, keycloakId);
    }
    
}
