package com.kolmir.identity_service.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kolmir.identity_service.dto.UserCreateRequest;
import com.kolmir.identity_service.dto.UserResponse;
import com.kolmir.identity_service.dto.UserUpdateRequest;
import com.kolmir.identity_service.exception.NotFoundException;
import com.kolmir.identity_service.mapper.UserMapper;
import com.kolmir.identity_service.model.User;
import com.kolmir.identity_service.repository.UserRepository;
import com.kolmir.identity_service.service.UserAuthProvider;
import com.kolmir.identity_service.util.UserConstants;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private UserAuthProvider userAuthProvider;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void getAll_shouldReturnMappedUsers() {
        User user = user(1L, "kc-1", "old@mail.com", "olduser", "Old Name", "old bio", true);
        UserResponse mapped = new UserResponse(1L, "old@mail.com", "olduser", "Old Name", "old bio", true);

        when(userRepository.findAll()).thenReturn(List.of(user));
        when(userMapper.toResponses(List.of(user))).thenReturn(List.of(mapped));

        List<UserResponse> result = userService.getAll();

        assertThat(result).containsExactly(mapped);
        verify(userRepository).findAll();
        verify(userMapper).toResponses(List.of(user));
    }

    @Test
    void getById_shouldReturnMappedUser() {
        User user = user(5L, "kc-5", "user@mail.com", "user5", "User Five", "bio", true);
        UserResponse response = new UserResponse(5L, "user@mail.com", "user5", "User Five", "bio", true);

        when(userRepository.findById(5L)).thenReturn(Optional.of(user));
        when(userMapper.toUserResponse(user)).thenReturn(response);

        UserResponse result = userService.getById(5L);

        assertThat(result).isEqualTo(response);
        verify(userRepository).findById(5L);
        verify(userMapper).toUserResponse(user);
    }

    @Test
    void getById_shouldThrowWhenUserNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getById(99L))
                .isInstanceOf(NotFoundException.class)
                .hasMessage(UserConstants.USER_ID_NOT_FOUND);
    }

    @Test
    void save_shouldPersistMappedUserWhenKeycloakIdReturned() {
        UserCreateRequest request = new UserCreateRequest("new@mail.com", "new_user", "pass1234", "New User", "bio");
        User mapped = user(null, null, "new@mail.com", "new_user", "New User", "bio", true);
        User saved = user(11L, "kc-11", "new@mail.com", "new_user", "New User", "bio", true);
        UserResponse response = new UserResponse(11L, "new@mail.com", "new_user", "New User", "bio", true);

        when(userAuthProvider.createUser(request)).thenReturn("kc-11");
        when(userMapper.toUser(request)).thenReturn(mapped);
        when(userRepository.save(mapped)).thenReturn(saved);
        when(userMapper.toUserResponse(saved)).thenReturn(response);

        UserResponse result = userService.save(request);

        assertThat(result).isEqualTo(response);
        assertThat(mapped.getKeycloakId()).isEqualTo("kc-11");
        verify(userAuthProvider).createUser(request);
        verify(userMapper).toUser(request);
        verify(userRepository).save(mapped);
        verify(userMapper).toUserResponse(saved);
    }

    @Test
    void save_shouldReturnNullWhenKeycloakIdIsEmpty() {
        UserCreateRequest request = new UserCreateRequest("x@mail.com", "x", "pass1234", "X", null);
        when(userAuthProvider.createUser(request)).thenReturn("");

        UserResponse result = userService.save(request);

        assertThat(result).isNull();
        verify(userAuthProvider).createUser(request);
        verify(userMapper, never()).toUser(any(UserCreateRequest.class));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void update_shouldApplyFieldsCallProviderAndSave() {
        UserUpdateRequest request = new UserUpdateRequest("new@mail.com", "new_user", "New Name", "new bio");
        User existing = user(3L, "kc-3", "old@mail.com", "old_user", "Old Name", "old bio", true);
        UserResponse response = new UserResponse(3L, "new@mail.com", "new_user", "New Name", "new bio", true);

        when(userRepository.findById(3L)).thenReturn(Optional.of(existing));
        when(userRepository.save(existing)).thenReturn(existing);
        when(userMapper.toUserResponse(existing)).thenReturn(response);

        UserResponse result = userService.update(3L, request);

        assertThat(result).isEqualTo(response);
        assertThat(existing.getEmail()).isEqualTo("new@mail.com");
        assertThat(existing.getUsername()).isEqualTo("new_user");
        assertThat(existing.getDisplayName()).isEqualTo("New Name");
        assertThat(existing.getBio()).isEqualTo("new bio");
        verify(userAuthProvider).changeUserInfo(existing);
        verify(userRepository).save(existing);
    }

    @Test
    void disable_shouldMarkDisabledCallProviderAndSave() {
        User existing = user(7L, "kc-7", "u@mail.com", "u7", "User Seven", "bio", true);
        UserResponse response = new UserResponse(7L, "u@mail.com", "u7", "User Seven", "bio", false);

        when(userRepository.findById(7L)).thenReturn(Optional.of(existing));
        when(userRepository.save(existing)).thenReturn(existing);
        when(userMapper.toUserResponse(existing)).thenReturn(response);

        UserResponse result = userService.disable(7L);

        assertThat(result).isEqualTo(response);
        assertThat(existing.getIsEnabled()).isFalse();
        verify(userAuthProvider).disableUser("kc-7");
        verify(userRepository).save(existing);
    }

    @Test
    void update_shouldThrowWhenUserNotFound() {
        when(userRepository.findById(77L)).thenReturn(Optional.empty());
        UserUpdateRequest request = new UserUpdateRequest("a@mail.com", "name", "disp", "bio");

        assertThatThrownBy(() -> userService.update(77L, request))
                .isInstanceOf(NotFoundException.class)
                .hasMessage(UserConstants.USER_ID_NOT_FOUND);
    }

    private static User user(
            Long id,
            String keycloakId,
            String email,
            String username,
            String displayName,
            String bio,
            boolean enabled
    ) {
        User user = new User();
        user.setId(id);
        user.setKeycloakId(keycloakId);
        user.setEmail(email);
        user.setUsername(username);
        user.setDisplayName(displayName);
        user.setBio(bio);
        user.setIsEnabled(enabled);
        return user;
    }
}
