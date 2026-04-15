package com.kolmir.identity_service.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static com.kolmir.identity_service.testutil.IdentityServiceTestObjectFactory.user;
import static com.kolmir.identity_service.testutil.IdentityServiceTestObjectFactory.userCreateRequest;
import static com.kolmir.identity_service.testutil.IdentityServiceTestObjectFactory.userResponse;
import static com.kolmir.identity_service.testutil.IdentityServiceTestObjectFactory.userUpdateRequest;
import static com.kolmir.identity_service.testutil.IdentityServiceTestConstants.BIO;
import static com.kolmir.identity_service.testutil.IdentityServiceTestConstants.BIO_NEW;
import static com.kolmir.identity_service.testutil.IdentityServiceTestConstants.DISPLAY_NAME;
import static com.kolmir.identity_service.testutil.IdentityServiceTestConstants.DISPLAY_NAME_NEW;
import static com.kolmir.identity_service.testutil.IdentityServiceTestConstants.EMAIL;
import static com.kolmir.identity_service.testutil.IdentityServiceTestConstants.EMAIL_NEW;
import static com.kolmir.identity_service.testutil.IdentityServiceTestConstants.EMPTY;
import static com.kolmir.identity_service.testutil.IdentityServiceTestConstants.KEYCLOAK_ID;
import static com.kolmir.identity_service.testutil.IdentityServiceTestConstants.KEYCLOAK_ID_ALT;
import static com.kolmir.identity_service.testutil.IdentityServiceTestConstants.KEYCLOAK_ID_CREATED;
import static com.kolmir.identity_service.testutil.IdentityServiceTestConstants.PASSWORD;
import static com.kolmir.identity_service.testutil.IdentityServiceTestConstants.USERNAME;
import static com.kolmir.identity_service.testutil.IdentityServiceTestConstants.USERNAME_NEW;
import static com.kolmir.identity_service.testutil.IdentityServiceTestConstants.USER_ID_1;
import static com.kolmir.identity_service.testutil.IdentityServiceTestConstants.USER_ID_3;
import static com.kolmir.identity_service.testutil.IdentityServiceTestConstants.USER_ID_5;
import static com.kolmir.identity_service.testutil.IdentityServiceTestConstants.USER_ID_7;
import static com.kolmir.identity_service.testutil.IdentityServiceTestConstants.USER_ID_11;
import static com.kolmir.identity_service.testutil.IdentityServiceTestConstants.USER_ID_77;
import static com.kolmir.identity_service.testutil.IdentityServiceTestConstants.USER_ID_99;
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

import com.kolmir.identity_service.dto.UserRegisterRequest;
import com.kolmir.identity_service.dto.UserResponse;
import com.kolmir.identity_service.dto.UserUpdateRequest;
import com.kolmir.identity_service.exception.NotFoundException;
import com.kolmir.identity_service.mapper.UserMapper;
import com.kolmir.identity_service.model.User;
import com.kolmir.identity_service.model.UserRole;
import com.kolmir.identity_service.repository.UserRepository;
import com.kolmir.identity_service.service.UserAuthProvider;
import com.kolmir.identity_service.util.UserUtils;

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
        User user = user(USER_ID_1, KEYCLOAK_ID, EMAIL, USERNAME, DISPLAY_NAME, BIO, true);
        UserResponse mapped = userResponse(USER_ID_1, EMAIL, USERNAME, DISPLAY_NAME, BIO, UserRole.USER, true);

        when(userRepository.findAll()).thenReturn(List.of(user));
        when(userMapper.toResponses(List.of(user))).thenReturn(List.of(mapped));

        List<UserResponse> result = userService.getAll();

        assertThat(result).containsExactly(mapped);
        verify(userRepository).findAll();
        verify(userMapper).toResponses(List.of(user));
    }

    @Test
    void getById_shouldReturnMappedUser() {
        User user = user(USER_ID_5, KEYCLOAK_ID, EMAIL, USERNAME, DISPLAY_NAME, BIO, true);
        UserResponse response = userResponse(USER_ID_5, EMAIL, USERNAME, DISPLAY_NAME, BIO, UserRole.USER, true);

        when(userRepository.findById(USER_ID_5)).thenReturn(Optional.of(user));
        when(userMapper.toUserResponse(user)).thenReturn(response);

        UserResponse result = userService.getById(USER_ID_5);

        assertThat(result).isEqualTo(response);
        verify(userRepository).findById(USER_ID_5);
        verify(userMapper).toUserResponse(user);
    }

    @Test
    void getById_shouldThrowWhenUserNotFound() {
        when(userRepository.findById(USER_ID_99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getById(USER_ID_99))
                .isInstanceOf(NotFoundException.class)
                .hasMessage(UserUtils.USER_ID_NOT_FOUND);
    }

    @Test
    void save_shouldPersistMappedUserWhenKeycloakIdReturned() {
        UserRegisterRequest request = userCreateRequest(EMAIL_NEW, USERNAME_NEW, PASSWORD, DISPLAY_NAME_NEW, BIO);
        User mapped = user(null, null, EMAIL_NEW, USERNAME_NEW, DISPLAY_NAME_NEW, BIO, true);
        User saved = user(USER_ID_11, KEYCLOAK_ID_CREATED, EMAIL_NEW, USERNAME_NEW, DISPLAY_NAME_NEW, BIO, true);
        UserResponse response = userResponse(USER_ID_11, EMAIL_NEW, USERNAME_NEW, DISPLAY_NAME_NEW, BIO, UserRole.USER, true);

        when(userAuthProvider.createUser(request)).thenReturn(KEYCLOAK_ID_CREATED);
        when(userMapper.toUser(request)).thenReturn(mapped);
        when(userRepository.save(mapped)).thenReturn(saved);
        when(userMapper.toUserResponse(saved)).thenReturn(response);

        UserResponse result = userService.saveRegisteredUser(request);

        assertThat(result).isEqualTo(response);
        assertThat(mapped.getKeycloakId()).isEqualTo(KEYCLOAK_ID_CREATED);
        verify(userAuthProvider).createUser(request);
        verify(userMapper).toUser(request);
        verify(userRepository).save(mapped);
        verify(userMapper).toUserResponse(saved);
    }

    @Test
    void save_shouldReturnNullWhenKeycloakIdIsEmpty() {
        UserRegisterRequest request = userCreateRequest(EMAIL, USERNAME, PASSWORD, DISPLAY_NAME, null);
        when(userAuthProvider.createUser(request)).thenReturn(EMPTY);

        UserResponse result = userService.saveRegisteredUser(request);

        assertThat(result).isNull();
        verify(userAuthProvider).createUser(request);
        verify(userMapper, never()).toUser(any(UserRegisterRequest.class));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void update_shouldApplyFieldsCallProviderAndSave() {
        UserUpdateRequest request = userUpdateRequest(EMAIL_NEW, USERNAME_NEW, DISPLAY_NAME_NEW, BIO_NEW);
        User existing = user(USER_ID_3, KEYCLOAK_ID_ALT, EMAIL, USERNAME, DISPLAY_NAME, BIO, true);
        UserResponse response = userResponse(USER_ID_3, EMAIL_NEW, USERNAME_NEW, DISPLAY_NAME_NEW, BIO_NEW, UserRole.USER, true);

        when(userRepository.findById(USER_ID_3)).thenReturn(Optional.of(existing));
        when(userRepository.save(existing)).thenReturn(existing);
        when(userMapper.toUserResponse(existing)).thenReturn(response);

        UserResponse result = userService.update(USER_ID_3, request);

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
        User existing = user(USER_ID_7, KEYCLOAK_ID_ALT, EMAIL, USERNAME, DISPLAY_NAME, BIO, true);
        UserResponse response = userResponse(USER_ID_7, EMAIL, USERNAME, DISPLAY_NAME, BIO, UserRole.USER, false);

        when(userRepository.findById(USER_ID_7)).thenReturn(Optional.of(existing));
        when(userRepository.save(existing)).thenReturn(existing);
        when(userMapper.toUserResponse(existing)).thenReturn(response);

        UserResponse result = userService.disable(USER_ID_7);

        assertThat(result).isEqualTo(response);
        assertThat(existing.getIsEnabled()).isFalse();
        verify(userAuthProvider).disableUser(KEYCLOAK_ID_ALT);
        verify(userRepository).save(existing);
    }

    @Test
    void update_shouldThrowWhenUserNotFound() {
        when(userRepository.findById(USER_ID_77)).thenReturn(Optional.empty());
        UserUpdateRequest request = userUpdateRequest(EMAIL, USERNAME, DISPLAY_NAME, BIO);

        assertThatThrownBy(() -> userService.update(USER_ID_77, request))
                .isInstanceOf(NotFoundException.class)
                .hasMessage(UserUtils.USER_ID_NOT_FOUND);
    }
}
