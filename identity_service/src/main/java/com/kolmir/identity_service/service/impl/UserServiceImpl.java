package com.kolmir.identity_service.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kolmir.identity_service.dto.UserCreateRequest;
import com.kolmir.identity_service.dto.UserRegisterRequest;
import com.kolmir.identity_service.dto.UserResponse;
import com.kolmir.identity_service.dto.UserUpdateRequest;
import com.kolmir.identity_service.exception.CreatingException;
import com.kolmir.identity_service.exception.NotFoundException;
import com.kolmir.identity_service.exception.UpdatingException;
import com.kolmir.identity_service.exception.AlreadyExistsException;
import com.kolmir.identity_service.exception.ChangingForbidenException;
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
    public List<UserResponse> getAll() {
        return userMapper.toResponses(userRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getById(Long id) {
        return userMapper.toUserResponse(getUserById(id));
    }

    @Override
    public UserResponse saveCreatedUser(UserCreateRequest request) {
        Long userId = saveRegisteredUser(userMapper.toUserRegisterRequest(request)).id();
        return changeRole(userId, request.role());
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
    public UserResponse changeRole(Long id, UserRole newRole) {
        if (newRole.equals(UserRole.MAIN_ADMIN))
            throw new ChangingForbidenException(NO_PERMISIONS_EXCEPTION_MESSAGE);

        User user = getUserById(id);
        UserRole oldRole = user.getRole();
        user.setRole(newRole);
        userAuthProvider.changeUserRole(user.getKeycloakId(), oldRole.getStringName(), newRole.getStringName());

        try {
            return userMapper.toUserResponse(userRepository.save(user));
        } catch (Exception e) {
            userAuthProvider.changeUserRole(user.getKeycloakId(), newRole.getStringName(), oldRole.getStringName());
            throw new UpdatingException(ROLE_CHANGING_EXCEPTION_MESSAGE + e.getMessage());
        }
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
