package com.kolmir.identity_service.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static com.kolmir.identity_service.testutil.IdentityTestObjectFactory.jwtWithSubject;
import static com.kolmir.identity_service.testutil.IdentityTestObjectFactory.keycloakTokenResponse;
import static com.kolmir.identity_service.testutil.IdentityTestObjectFactory.refreshTokenRequest;
import static com.kolmir.identity_service.testutil.IdentityTestObjectFactory.user;
import static com.kolmir.identity_service.testutil.IdentityTestObjectFactory.userAuthRequest;
import static com.kolmir.identity_service.testutil.IdentityTestObjectFactory.userAuthResponse;
import static com.kolmir.identity_service.testutil.IdentityTestObjectFactory.userCreateRequest;
import static com.kolmir.identity_service.testutil.IdentityTestObjectFactory.userResponse;
import static com.kolmir.identity_service.testutil.IdentityTestStringConstants.ACCESS_TOKEN;
import static com.kolmir.identity_service.testutil.IdentityTestStringConstants.BIO;
import static com.kolmir.identity_service.testutil.IdentityTestStringConstants.DISPLAY_NAME;
import static com.kolmir.identity_service.testutil.IdentityTestStringConstants.EMAIL;
import static com.kolmir.identity_service.testutil.IdentityTestStringConstants.KEYCLOAK_ID;
import static com.kolmir.identity_service.testutil.IdentityTestStringConstants.KEYCLOAK_ID_ALT;
import static com.kolmir.identity_service.testutil.IdentityTestStringConstants.PASSWORD;
import static com.kolmir.identity_service.testutil.IdentityTestStringConstants.REFRESH_TOKEN;
import static com.kolmir.identity_service.testutil.IdentityTestStringConstants.REFRESH_TOKEN_VALUE;
import static com.kolmir.identity_service.testutil.IdentityTestStringConstants.USERNAME;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

import com.kolmir.identity_service.dto.RefreshTokenRequest;
import com.kolmir.identity_service.dto.UserAuthRequest;
import com.kolmir.identity_service.dto.UserAuthResponse;
import com.kolmir.identity_service.dto.UserCreateRequest;
import com.kolmir.identity_service.dto.UserRegisterResponse;
import com.kolmir.identity_service.dto.UserResponse;
import com.kolmir.identity_service.mapper.AuthMapper;
import com.kolmir.identity_service.mapper.UserMapper;
import com.kolmir.identity_service.model.User;
import com.kolmir.identity_service.model.UserRole;
import com.kolmir.identity_service.repository.UserRepository;
import com.kolmir.identity_service.service.UserAuthProvider;
import com.kolmir.identity_service.service.UserService;

@ExtendWith(MockitoExtension.class)
class SecurityServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserService userService;

    @Mock
    private UserAuthProvider userAuthProvider;

    @Mock
    private AuthMapper authMapper;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private SecurityServiceImpl securityService;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void isCurrentUserOwner_shouldCheckUserIdAndKeycloakIdFromJwt() {
        setJwtSubject(KEYCLOAK_ID);
        when(userRepository.existsByIdAndKeycloakId(100L, KEYCLOAK_ID)).thenReturn(true);

        boolean result = securityService.isCurrentUserOwner(100L);

        assertThat(result).isTrue();
        verify(userRepository).existsByIdAndKeycloakId(100L, KEYCLOAK_ID);
    }

    @Test
    void register_shouldAuthenticateAndCreateUser() {
        UserCreateRequest createRequest = userCreateRequest(EMAIL, USERNAME, PASSWORD, DISPLAY_NAME, BIO);
        UserAuthRequest authRequest = userAuthRequest(USERNAME, PASSWORD);
        Map<String, Object> keycloakResponse = keycloakTokenResponse(ACCESS_TOKEN, REFRESH_TOKEN, 300, 900);
        UserAuthResponse authResponse = userAuthResponse(ACCESS_TOKEN, 300, REFRESH_TOKEN, 900);
        UserResponse userResponse = userResponse(10L, EMAIL, USERNAME, DISPLAY_NAME, BIO, UserRole.USER, true);

        when(authMapper.userCreateRequestToUserAuthRequest(createRequest)).thenReturn(authRequest);
        when(userAuthProvider.getTokensForUser(USERNAME, PASSWORD)).thenReturn(keycloakResponse);
        when(authMapper.keycloakResponseToUserAuth(keycloakResponse)).thenReturn(authResponse);
        when(userService.save(createRequest)).thenReturn(userResponse);

        UserRegisterResponse result = securityService.register(createRequest);

        assertThat(result.auth()).isEqualTo(authResponse);
        assertThat(result.user()).isEqualTo(userResponse);
        verify(authMapper).userCreateRequestToUserAuthRequest(createRequest);
        verify(userAuthProvider).getTokensForUser(USERNAME, PASSWORD);
        verify(userService).save(createRequest);
    }

    @Test
    void login_shouldReturnTokensMappedFromProviderResponse() {
        UserAuthRequest request = userAuthRequest(USERNAME, PASSWORD);
        Map<String, Object> keycloakResponse = keycloakTokenResponse(ACCESS_TOKEN, REFRESH_TOKEN, 300, 900);
        UserAuthResponse authResponse = userAuthResponse(ACCESS_TOKEN, 300, REFRESH_TOKEN, 900);

        when(userAuthProvider.getTokensForUser(USERNAME, PASSWORD)).thenReturn(keycloakResponse);
        when(authMapper.keycloakResponseToUserAuth(keycloakResponse)).thenReturn(authResponse);

        UserAuthResponse result = securityService.login(request);

        assertThat(result).isEqualTo(authResponse);
        verify(userAuthProvider).getTokensForUser(USERNAME, PASSWORD);
        verify(authMapper).keycloakResponseToUserAuth(keycloakResponse);
    }

    @Test
    void refresh_shouldReturnMappedTokens() {
        RefreshTokenRequest request = refreshTokenRequest(REFRESH_TOKEN_VALUE);
        Map<String, Object> keycloakResponse = keycloakTokenResponse(ACCESS_TOKEN, REFRESH_TOKEN, 120, 720);
        UserAuthResponse authResponse = userAuthResponse(ACCESS_TOKEN, 120, REFRESH_TOKEN, 720);

        when(userAuthProvider.refreshUserToken(REFRESH_TOKEN_VALUE)).thenReturn(keycloakResponse);
        when(authMapper.keycloakResponseToUserAuth(keycloakResponse)).thenReturn(authResponse);

        UserAuthResponse result = securityService.refresh(request);

        assertThat(result).isEqualTo(authResponse);
        verify(userAuthProvider).refreshUserToken(REFRESH_TOKEN_VALUE);
        verify(authMapper).keycloakResponseToUserAuth(keycloakResponse);
    }

    @Test
    void getUserFromPrincipal_shouldReadUserByJwtSubject() {
        setJwtSubject(KEYCLOAK_ID_ALT);
        User user = user(55L, KEYCLOAK_ID_ALT, EMAIL, USERNAME, null, null, true);
        UserResponse mapped = userResponse(55L, EMAIL, USERNAME, DISPLAY_NAME, BIO, UserRole.USER, true);

        when(userRepository.getUserByKeycloakId(KEYCLOAK_ID_ALT)).thenReturn(user);
        when(userMapper.toUserResponse(user)).thenReturn(mapped);

        UserResponse result = securityService.getUserFromPrincipal();

        assertThat(result).isEqualTo(mapped);
        verify(userRepository).getUserByKeycloakId(KEYCLOAK_ID_ALT);
        verify(userMapper).toUserResponse(user);
    }

    private static void setJwtSubject(String subject) {
        Jwt jwt = jwtWithSubject(subject);
        SecurityContextHolder.getContext()
                .setAuthentication(new UsernamePasswordAuthenticationToken(jwt, null));
    }
}
