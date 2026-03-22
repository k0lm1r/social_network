package com.kolmir.identity_service.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kolmir.identity_service.dto.UserCreateRequest;
import com.kolmir.identity_service.dto.UserResponse;
import com.kolmir.identity_service.dto.UserUpdateRequest;
import com.kolmir.identity_service.exception.NotFoundException;
import com.kolmir.identity_service.mapper.UserMapper;
import com.kolmir.identity_service.model.User;
import com.kolmir.identity_service.repository.UserRepository;
import com.kolmir.identity_service.service.UserService;
import com.kolmir.identity_service.service.impl.util.UserFieldSetter;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    
    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getAll() {
        return userMapper.toResponses(userRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getById(Long id) {
        return userMapper.toUserResponse(getUserById(id));
    }

    @Override
    public UserResponse save(UserCreateRequest request) {
        return userMapper.toUserResponse(userRepository.save(userMapper.toUser(request)));
    }

    @Override
    public UserResponse update(Long id, UserUpdateRequest request) {
        User user = getUserById(id);
        user = UserFieldSetter.setFromUpdateRequest(user, request);
        return userMapper.toUserResponse(userRepository.save(user));
    }

    @Override
    public UserResponse disable(Long id) {
        User user = getUserById(id);
        user.setIsEnabled(false);
        return userMapper.toUserResponse(userRepository.save(user));
    }

    private User getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(
                    () -> new NotFoundException("User with this id was not found")
                );
        return user;
    }

}
