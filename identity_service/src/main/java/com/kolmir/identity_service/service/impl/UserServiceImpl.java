package com.kolmir.identity_service.service.impl;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kolmir.identity_service.dto.auth.UserRegisterRequest;
import com.kolmir.identity_service.dto.user.UserChangeRoleRequest;
import com.kolmir.identity_service.dto.user.UserCreateRequest;
import com.kolmir.identity_service.dto.user.UserResponse;
import com.kolmir.identity_service.dto.user.UserUpdateRequest;
import com.kolmir.identity_service.exception.CreatingException;
import com.kolmir.identity_service.exception.NotFoundException;
import com.kolmir.identity_service.exception.UpdatingException;
import com.kolmir.identity_service.exception.AlreadyExistsException;
import com.kolmir.identity_service.mapper.UserMapper;
import com.kolmir.identity_service.model.User;
import com.kolmir.identity_service.model.UserRole;
import com.kolmir.identity_service.repository.UserRepository;
import com.kolmir.identity_service.service.UserAuthProvider;
import com.kolmir.identity_service.service.UserService;
import com.kolmir.identity_service.service.impl.util.UserFieldSetter;
import static com.kolmir.identity_service.util.UserUtils.*;

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
    public Page<UserResponse> getAll(Pageable pageable) {
        return userRepository.findAll(pageable).map(userMapper::toUserResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getById(Long id) {
        return userMapper.toUserResponse(getUserById(id));
    }

    @Override
    public UserResponse saveCreatedUser(UserCreateRequest request) {
        Long userId = saveRegisteredUser(userMapper.toUserRegisterRequest(request)).id();
        return changeRole(userId, new UserChangeRoleRequest(request.role()));
    }

    @Override
    public UserResponse saveRegisteredUser(UserRegisterRequest request) {
        String keycloakId = userAuthProvider.createUser(request);
        
        if (!keycloakId.isEmpty()) {
            User user = userMapper.toUser(request);
            user.setKeycloakId(keycloakId);
            try {
                return userMapper.toUserResponse(userRepository.save(user));
            } catch (Exception e) {
                userAuthProvider.deleteUser(keycloakId);
                throw new CreatingException(CREATING_EXCEPTION_MESSAGE + e.getMessage());
            }
        }

        return null;
    }

    @Override
    public UserResponse update(Long id, UserUpdateRequest request) {
        User user = getUserById(id);
        user = UserFieldSetter.setFromUpdateRequest(user, request);

        if (isNewDataValid(user)) {
            userAuthProvider.changeUserInfo(user);
            return userMapper.toUserResponse(userRepository.save(user));
        }
        
        return null;
    }

    @Override
    public UserResponse disable(Long id) {
        User user = getUserById(id);
        user.setIsEnabled(false);
        UserResponse response = userMapper.toUserResponse(userRepository.save(user));
        userAuthProvider.disableUser(user.getKeycloakId());
        return response;
    }

    @Override
    public UserResponse changeRole(Long id, UserChangeRoleRequest request) {
        User user = getUserById(id);
        UserRole oldRole = user.getRole();
        user.setRole(UserRole.valueOf(request.newRole()));
        userAuthProvider.changeUserRole(user.getKeycloakId(), oldRole.getStringName(), request.newRole());

        try {
            return userMapper.toUserResponse(userRepository.save(user));
        } catch (Exception e) {
            userAuthProvider.changeUserRole(user.getKeycloakId(), request.newRole(), oldRole.getStringName());
            throw new UpdatingException(ROLE_CHANGING_EXCEPTION_MESSAGE + e.getMessage());
        }
    }
    
    @Override
    public Boolean isUserExists(Long id) {
        return userRepository.existsById(id);
    }

    private User getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(
                    () -> new NotFoundException(USER_ID_NOT_FOUND)
                );
        return user;
    }

    private boolean isNewDataValid(User user) {
        Optional<User> findedByUsernameUser = userRepository.findByUsernameIgnoreCase(user.getUsername());
        if (findedByUsernameUser.isPresent() && !findedByUsernameUser.get().getId().equals(user.getId()))
            throw new AlreadyExistsException(USERNAME_IN_USE_MESSAGE);

        Optional<User> findedByEmailUser = userRepository.findByEmailIgnoreCase(user.getEmail());
        if (findedByEmailUser.isPresent() && !findedByEmailUser.get().getId().equals(user.getId()))
            throw new AlreadyExistsException(EMAIL_IN_USE_MESSAGE);

        return true;
    }
}
