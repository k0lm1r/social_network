package com.kolmir.identity_service.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
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
        setJwtSubject("kc-100");
        when(userRepository.existsByIdAndKeycloakId(100L, "kc-100")).thenReturn(true);

        boolean result = securityService.isCurrentUserOwner(100L);

        assertThat(result).isTrue();
        verify(userRepository).existsByIdAndKeycloakId(100L, "kc-100");
    }

    @Test
    void register_shouldAuthenticateAndCreateUser() {
        UserCreateRequest createRequest = new UserCreateRequest("mail@test.com", "user", "pass1234", "Disp", "bio");
        UserAuthRequest authRequest = new UserAuthRequest("user", "pass1234");
        Map<String, Object> keycloakResponse = Map.of(
                "access_token", "a",
                "refresh_token", "r",
                "expires_in", 300,
                "refresh_expires_in", 900
        );
        UserAuthResponse authResponse = new UserAuthResponse("a", 300, "r", 900);
        UserResponse userResponse = new UserResponse(10L, "mail@test.com", "user", "Disp", "bio", true);

        when(authMapper.userCreateRequestToUserAuthRequest(createRequest)).thenReturn(authRequest);
        when(userAuthProvider.getTokensForUser("user", "pass1234")).thenReturn(keycloakResponse);
        when(authMapper.keycloakResponseToUserAuth(keycloakResponse)).thenReturn(authResponse);
        when(userService.save(createRequest)).thenReturn(userResponse);

        UserRegisterResponse result = securityService.register(createRequest);

        assertThat(result.auth()).isEqualTo(authResponse);
        assertThat(result.user()).isEqualTo(userResponse);
        verify(authMapper).userCreateRequestToUserAuthRequest(createRequest);
        verify(userAuthProvider).getTokensForUser("user", "pass1234");
        verify(userService).save(createRequest);
    }

    @Test
    void login_shouldReturnTokensMappedFromProviderResponse() {
        UserAuthRequest request = new UserAuthRequest("user", "pass1234");
        Map<String, Object> keycloakResponse = Map.of(
                "access_token", "a",
                "refresh_token", "r",
                "expires_in", 300,
                "refresh_expires_in", 900
        );
        UserAuthResponse authResponse = new UserAuthResponse("a", 300, "r", 900);

        when(userAuthProvider.getTokensForUser("user", "pass1234")).thenReturn(keycloakResponse);
        when(authMapper.keycloakResponseToUserAuth(keycloakResponse)).thenReturn(authResponse);

        UserAuthResponse result = securityService.login(request);

        assertThat(result).isEqualTo(authResponse);
        verify(userAuthProvider).getTokensForUser("user", "pass1234");
        verify(authMapper).keycloakResponseToUserAuth(keycloakResponse);
    }

    @Test
    void refresh_shouldReturnMappedTokens() {
        RefreshTokenRequest request = new RefreshTokenRequest("refresh-token");
        Map<String, Object> keycloakResponse = Map.of(
                "access_token", "a2",
                "refresh_token", "r2",
                "expires_in", 120,
                "refresh_expires_in", 720
        );
        UserAuthResponse authResponse = new UserAuthResponse("a2", 120, "r2", 720);

        when(userAuthProvider.refreshUserToken("refresh-token")).thenReturn(keycloakResponse);
        when(authMapper.keycloakResponseToUserAuth(keycloakResponse)).thenReturn(authResponse);

        UserAuthResponse result = securityService.refresh(request);

        assertThat(result).isEqualTo(authResponse);
        verify(userAuthProvider).refreshUserToken("refresh-token");
        verify(authMapper).keycloakResponseToUserAuth(keycloakResponse);
    }

    @Test
    void getUserFromPrincipal_shouldReadUserByJwtSubject() {
        setJwtSubject("kc-55");
        User user = new User();
        user.setId(55L);
        user.setKeycloakId("kc-55");
        user.setEmail("mail55@test.com");
        user.setUsername("u55");
        UserResponse mapped = new UserResponse(55L, "mail55@test.com", "u55", "d", "b", true);

        when(userRepository.getUserByKeycloakId("kc-55")).thenReturn(user);
        when(userMapper.toUserResponse(user)).thenReturn(mapped);

        UserResponse result = securityService.getUserFromPrincipal();

        assertThat(result).isEqualTo(mapped);
        verify(userRepository).getUserByKeycloakId("kc-55");
        verify(userMapper).toUserResponse(user);
    }

    private static void setJwtSubject(String subject) {
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .subject(subject)
                .claim("sub", subject)
                .build();
        SecurityContextHolder.getContext()
                .setAuthentication(new UsernamePasswordAuthenticationToken(jwt, null));
    }
}
