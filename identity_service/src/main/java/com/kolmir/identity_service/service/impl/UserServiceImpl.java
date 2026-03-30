package com.kolmir.identity_service.service.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kolmir.identity_service.dto.UserCreateRequest;
import com.kolmir.identity_service.dto.UserResponse;
import com.kolmir.identity_service.dto.UserUpdateRequest;
import com.kolmir.identity_service.exception.NotFoundException;
import com.kolmir.identity_service.mapper.UserMapper;
import com.kolmir.identity_service.model.User;
import com.kolmir.identity_service.repository.UserRepository;
import com.kolmir.identity_service.service.UserAuthProvider;
import com.kolmir.identity_service.service.UserService;
import com.kolmir.identity_service.service.impl.util.UserFieldSetter;
import com.kolmir.identity_service.util.UserConstants;

import lombok.RequiredArgsConstructor;


@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final UserAuthProvider userAuthProvider;
    
    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponse> getAll() {
        return userMapper.toResponses(userRepository.findAll());
    }
    
    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@userService.isCurrentUserOwner(#id) || hasRole('ADMIN')")
    public UserResponse getById(Long id) {
        return userMapper.toUserResponse(getUserById(id));
    }

    @Override
    public UserResponse save(UserCreateRequest request) {
        String keycloakId = userAuthProvider.createUser(request);
        if (!keycloakId.isEmpty()) {
            User user = userMapper.toUser(request);
            user.setKeycloakId(UUID.fromString(keycloakId));
            return userMapper.toUserResponse(userRepository.save(user));
        }
        return null;
    }

    @Override
    @PreAuthorize("@userService.isCurrentUserOwner(#id) || hasRole('ADMIN')")
    public UserResponse update(Long id, UserUpdateRequest request) {
        User user = getUserById(id);
        user = UserFieldSetter.setFromUpdateRequest(user, request);
        userAuthProvider.changeUserInfo(user);
        return userMapper.toUserResponse(userRepository.save(user));
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse disable(Long id) {
        User user = getUserById(id);
        user.setIsEnabled(false);
        userAuthProvider.disableUser(user.getKeycloakId());
        return userMapper.toUserResponse(userRepository.save(user));
    }

    private User getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(
                    () -> new NotFoundException(UserConstants.USER_ID_NOT_FOUND)
                );
        return user;
    }

    public boolean isCurrentUserOwner(Long id) {
        Jwt jwt = (Jwt) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        String keycloakId = jwt.getSubject();
        return userRepository.existsByIdAndKeycloakId(id, UUID.fromString(keycloakId));
    }

}
