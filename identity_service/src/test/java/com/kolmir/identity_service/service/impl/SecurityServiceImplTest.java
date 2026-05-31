package com.kolmir.identity_service.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static com.kolmir.identity_service.testutil.IdentityServiceTestObjectFactory.jwtWithSubject;
import static com.kolmir.identity_service.testutil.IdentityServiceTestObjectFactory.keycloakTokenResponse;
import static com.kolmir.identity_service.testutil.IdentityServiceTestObjectFactory.refreshTokenRequest;
import static com.kolmir.identity_service.testutil.IdentityServiceTestObjectFactory.user;
import static com.kolmir.identity_service.testutil.IdentityServiceTestObjectFactory.userAuthRequest;
import static com.kolmir.identity_service.testutil.IdentityServiceTestObjectFactory.userAuthResponse;
import static com.kolmir.identity_service.testutil.IdentityServiceTestObjectFactory.userCreateRequest;
import static com.kolmir.identity_service.testutil.IdentityServiceTestObjectFactory.userResponse;
import static com.kolmir.identity_service.testutil.IdentityServiceTestConstants.ACCESS_TOKEN;
import static com.kolmir.identity_service.testutil.IdentityServiceTestConstants.BIO;
import static com.kolmir.identity_service.testutil.IdentityServiceTestConstants.DISPLAY_NAME;
import static com.kolmir.identity_service.testutil.IdentityServiceTestConstants.EMAIL;
import static com.kolmir.identity_service.testutil.IdentityServiceTestConstants.KEYCLOAK_ID;
import static com.kolmir.identity_service.testutil.IdentityServiceTestConstants.KEYCLOAK_ID_ALT;
import static com.kolmir.identity_service.testutil.IdentityServiceTestConstants.PASSWORD;
import static com.kolmir.identity_service.testutil.IdentityServiceTestConstants.REFRESH_TOKEN;
import static com.kolmir.identity_service.testutil.IdentityServiceTestConstants.REFRESH_TOKEN_VALUE;
import static com.kolmir.identity_service.testutil.IdentityServiceTestConstants.USERNAME;
import static com.kolmir.identity_service.testutil.IdentityServiceTestConstants.ACCESS_TOKEN_EXPIRES_IN;
import static com.kolmir.identity_service.testutil.IdentityServiceTestConstants.REFRESH_TOKEN_EXPIRES_IN;
import static com.kolmir.identity_service.testutil.IdentityServiceTestConstants.REFRESHED_ACCESS_TOKEN_EXPIRES_IN;
import static com.kolmir.identity_service.testutil.IdentityServiceTestConstants.REFRESHED_REFRESH_TOKEN_EXPIRES_IN;
import static com.kolmir.identity_service.testutil.IdentityServiceTestConstants.USER_ID_10;
import static com.kolmir.identity_service.testutil.IdentityServiceTestConstants.USER_ID_55;
import static com.kolmir.identity_service.testutil.IdentityServiceTestConstants.USER_ID_100;
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

import com.kolmir.auth.model.UserRole;
import com.kolmir.identity_service.dto.auth.RefreshTokenRequest;
import com.kolmir.identity_service.dto.auth.UserAuthRequest;
import com.kolmir.identity_service.dto.auth.UserAuthResponse;
import com.kolmir.identity_service.dto.auth.UserRegisterRequest;
import com.kolmir.identity_service.dto.auth.UserRegisterResponse;
import com.kolmir.identity_service.dto.user.UserResponse;
import com.kolmir.identity_service.mapper.AuthMapper;
import com.kolmir.identity_service.mapper.UserMapper;
import com.kolmir.identity_service.model.User;
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
        when(userRepository.existsByIdAndKeycloakId(USER_ID_100, KEYCLOAK_ID)).thenReturn(true);

        boolean result = securityService.isCurrentUserOwner(USER_ID_100);

        assertThat(result).isTrue();
        verify(userRepository).existsByIdAndKeycloakId(USER_ID_100, KEYCLOAK_ID);
    }

    @Test
    void register_shouldAuthenticateAndCreateUser() {
        UserRegisterRequest createRequest = userCreateRequest(EMAIL, USERNAME, PASSWORD, DISPLAY_NAME, BIO);
        UserAuthRequest authRequest = userAuthRequest(USERNAME, PASSWORD);
        Map<String, Object> keycloakResponse = keycloakTokenResponse(ACCESS_TOKEN, REFRESH_TOKEN, ACCESS_TOKEN_EXPIRES_IN, REFRESH_TOKEN_EXPIRES_IN);
        UserAuthResponse authResponse = userAuthResponse(ACCESS_TOKEN, ACCESS_TOKEN_EXPIRES_IN, REFRESH_TOKEN, REFRESH_TOKEN_EXPIRES_IN);
        UserResponse userResponse = userResponse(USER_ID_10, EMAIL, USERNAME, DISPLAY_NAME, BIO, UserRole.USER, true);

        when(authMapper.userCreateRequestToUserAuthRequest(createRequest)).thenReturn(authRequest);
        when(userAuthProvider.getTokensForUser(USERNAME, PASSWORD)).thenReturn(keycloakResponse);
        when(authMapper.keycloakResponseToUserAuth(keycloakResponse)).thenReturn(authResponse);
        when(userService.saveRegisteredUser(createRequest)).thenReturn(userResponse);

        UserRegisterResponse result = securityService.register(createRequest);

        assertThat(result.auth()).isEqualTo(authResponse);
        assertThat(result.user()).isEqualTo(userResponse);
        verify(authMapper).userCreateRequestToUserAuthRequest(createRequest);
        verify(userAuthProvider).getTokensForUser(USERNAME, PASSWORD);
        verify(userService).saveRegisteredUser(createRequest);
    }

    @Test
    void login_shouldReturnTokensMappedFromProviderResponse() {
        UserAuthRequest request = userAuthRequest(USERNAME, PASSWORD);
        Map<String, Object> keycloakResponse = keycloakTokenResponse(ACCESS_TOKEN, REFRESH_TOKEN, ACCESS_TOKEN_EXPIRES_IN, REFRESH_TOKEN_EXPIRES_IN);
        UserAuthResponse authResponse = userAuthResponse(ACCESS_TOKEN, ACCESS_TOKEN_EXPIRES_IN, REFRESH_TOKEN, REFRESH_TOKEN_EXPIRES_IN);

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
        Map<String, Object> keycloakResponse = keycloakTokenResponse(ACCESS_TOKEN, REFRESH_TOKEN, REFRESHED_ACCESS_TOKEN_EXPIRES_IN, REFRESHED_REFRESH_TOKEN_EXPIRES_IN);
        UserAuthResponse authResponse = userAuthResponse(ACCESS_TOKEN, REFRESHED_ACCESS_TOKEN_EXPIRES_IN, REFRESH_TOKEN, REFRESHED_REFRESH_TOKEN_EXPIRES_IN);

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
        User user = user(USER_ID_55, KEYCLOAK_ID_ALT, EMAIL, USERNAME, null, null, true);
        UserResponse mapped = userResponse(USER_ID_55, EMAIL, USERNAME, DISPLAY_NAME, BIO, UserRole.USER, true);

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
