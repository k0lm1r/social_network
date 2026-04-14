package com.kolmir.identity_service.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static com.kolmir.identity_service.testutil.IdentityTestObjectFactory.user;
import static com.kolmir.identity_service.testutil.IdentityTestObjectFactory.userCreateRequest;
import static com.kolmir.identity_service.testutil.IdentityTestObjectFactory.userResponse;
import static com.kolmir.identity_service.testutil.IdentityTestObjectFactory.userUpdateRequest;
import static com.kolmir.identity_service.testutil.IdentityTestStringConstants.BIO;
import static com.kolmir.identity_service.testutil.IdentityTestStringConstants.BIO_NEW;
import static com.kolmir.identity_service.testutil.IdentityTestStringConstants.DISPLAY_NAME;
import static com.kolmir.identity_service.testutil.IdentityTestStringConstants.DISPLAY_NAME_NEW;
import static com.kolmir.identity_service.testutil.IdentityTestStringConstants.EMAIL;
import static com.kolmir.identity_service.testutil.IdentityTestStringConstants.EMAIL_NEW;
import static com.kolmir.identity_service.testutil.IdentityTestStringConstants.EMPTY;
import static com.kolmir.identity_service.testutil.IdentityTestStringConstants.KEYCLOAK_ID;
import static com.kolmir.identity_service.testutil.IdentityTestStringConstants.KEYCLOAK_ID_ALT;
import static com.kolmir.identity_service.testutil.IdentityTestStringConstants.KEYCLOAK_ID_CREATED;
import static com.kolmir.identity_service.testutil.IdentityTestStringConstants.PASSWORD;
import static com.kolmir.identity_service.testutil.IdentityTestStringConstants.USERNAME;
import static com.kolmir.identity_service.testutil.IdentityTestStringConstants.USERNAME_NEW;
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
import com.kolmir.identity_service.model.UserRole;
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
        User user = user(1L, KEYCLOAK_ID, EMAIL, USERNAME, DISPLAY_NAME, BIO, true);
        UserResponse mapped = userResponse(1L, EMAIL, USERNAME, DISPLAY_NAME, BIO, UserRole.USER, true);

        when(userRepository.findAll()).thenReturn(List.of(user));
        when(userMapper.toResponses(List.of(user))).thenReturn(List.of(mapped));

        List<UserResponse> result = userService.getAll();

        assertThat(result).containsExactly(mapped);
        verify(userRepository).findAll();
        verify(userMapper).toResponses(List.of(user));
    }

    @Test
    void getById_shouldReturnMappedUser() {
        User user = user(5L, KEYCLOAK_ID, EMAIL, USERNAME, DISPLAY_NAME, BIO, true);
        UserResponse response = userResponse(5L, EMAIL, USERNAME, DISPLAY_NAME, BIO, UserRole.USER, true);

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
        UserCreateRequest request = userCreateRequest(EMAIL_NEW, USERNAME_NEW, PASSWORD, DISPLAY_NAME_NEW, BIO);
        User mapped = user(null, null, EMAIL_NEW, USERNAME_NEW, DISPLAY_NAME_NEW, BIO, true);
        User saved = user(11L, KEYCLOAK_ID_CREATED, EMAIL_NEW, USERNAME_NEW, DISPLAY_NAME_NEW, BIO, true);
        UserResponse response = userResponse(11L, EMAIL_NEW, USERNAME_NEW, DISPLAY_NAME_NEW, BIO, UserRole.USER, true);

        when(userAuthProvider.createUser(request)).thenReturn(KEYCLOAK_ID_CREATED);
        when(userMapper.toUser(request)).thenReturn(mapped);
        when(userRepository.save(mapped)).thenReturn(saved);
        when(userMapper.toUserResponse(saved)).thenReturn(response);

        UserResponse result = userService.save(request);

        assertThat(result).isEqualTo(response);
        assertThat(mapped.getKeycloakId()).isEqualTo(KEYCLOAK_ID_CREATED);
        verify(userAuthProvider).createUser(request);
        verify(userMapper).toUser(request);
        verify(userRepository).save(mapped);
        verify(userMapper).toUserResponse(saved);
    }

    @Test
    void save_shouldReturnNullWhenKeycloakIdIsEmpty() {
        UserCreateRequest request = userCreateRequest(EMAIL, USERNAME, PASSWORD, DISPLAY_NAME, null);
        when(userAuthProvider.createUser(request)).thenReturn(EMPTY);

        UserResponse result = userService.save(request);

        assertThat(result).isNull();
        verify(userAuthProvider).createUser(request);
        verify(userMapper, never()).toUser(any(UserCreateRequest.class));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void update_shouldApplyFieldsCallProviderAndSave() {
        UserUpdateRequest request = userUpdateRequest(EMAIL_NEW, USERNAME_NEW, DISPLAY_NAME_NEW, BIO_NEW);
        User existing = user(3L, KEYCLOAK_ID_ALT, EMAIL, USERNAME, DISPLAY_NAME, BIO, true);
        UserResponse response = userResponse(3L, EMAIL_NEW, USERNAME_NEW, DISPLAY_NAME_NEW, BIO_NEW, UserRole.USER, true);

        when(userRepository.findById(3L)).thenReturn(Optional.of(existing));
        when(userRepository.save(existing)).thenReturn(existing);
        when(userMapper.toUserResponse(existing)).thenReturn(response);

        UserResponse result = userService.update(3L, request);

        assertThat(result).isEqualTo(response);
        assertThat(existing.getEmail()).isEqualTo(EMAIL_NEW);
        assertThat(existing.getUsername()).isEqualTo(USERNAME_NEW);
        assertThat(existing.getDisplayName()).isEqualTo(DISPLAY_NAME_NEW);
        assertThat(existing.getBio()).isEqualTo(BIO_NEW);
        verify(userAuthProvider).changeUserInfo(existing);
        verify(userRepository).save(existing);
    }

    @Test
    void disable_shouldMarkDisabledCallProviderAndSave() {
        User existing = user(7L, KEYCLOAK_ID_ALT, EMAIL, USERNAME, DISPLAY_NAME, BIO, true);
        UserResponse response = userResponse(7L, EMAIL, USERNAME, DISPLAY_NAME, BIO, UserRole.USER, false);

        when(userRepository.findById(7L)).thenReturn(Optional.of(existing));
        when(userRepository.save(existing)).thenReturn(existing);
        when(userMapper.toUserResponse(existing)).thenReturn(response);

        UserResponse result = userService.disable(7L);

        assertThat(result).isEqualTo(response);
        assertThat(existing.getIsEnabled()).isFalse();
        verify(userAuthProvider).disableUser(KEYCLOAK_ID_ALT);
        verify(userRepository).save(existing);
    }

    @Test
    void update_shouldThrowWhenUserNotFound() {
        when(userRepository.findById(77L)).thenReturn(Optional.empty());
        UserUpdateRequest request = userUpdateRequest(EMAIL, USERNAME, DISPLAY_NAME, BIO);

        assertThatThrownBy(() -> userService.update(77L, request))
                .isInstanceOf(NotFoundException.class)
                .hasMessage(UserConstants.USER_ID_NOT_FOUND);
    }
}
