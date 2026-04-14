package com.kolmir.identity_service.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static com.kolmir.identity_service.testutil.IdentityTestObjectFactory.roleRepresentation;
import static com.kolmir.identity_service.testutil.IdentityTestObjectFactory.user;
import static com.kolmir.identity_service.testutil.IdentityTestObjectFactory.userCreateRequest;
import static com.kolmir.identity_service.testutil.IdentityTestObjectFactory.userRepresentation;
import static com.kolmir.identity_service.testutil.IdentityTestStringConstants.BIO;
import static com.kolmir.identity_service.testutil.IdentityTestStringConstants.CLIENT_ID;
import static com.kolmir.identity_service.testutil.IdentityTestStringConstants.CLIENT_SECRET;
import static com.kolmir.identity_service.testutil.IdentityTestStringConstants.DISPLAY_NAME;
import static com.kolmir.identity_service.testutil.IdentityTestStringConstants.DISPLAY_NAME_NEW;
import static com.kolmir.identity_service.testutil.IdentityTestStringConstants.EMAIL;
import static com.kolmir.identity_service.testutil.IdentityTestStringConstants.EMAIL_NEW;
import static com.kolmir.identity_service.testutil.IdentityTestStringConstants.KEYCLOAK_ID_ALT;
import static com.kolmir.identity_service.testutil.IdentityTestStringConstants.KEYCLOAK_ID_CREATED;
import static com.kolmir.identity_service.testutil.IdentityTestStringConstants.LOCATION_HEADER;
import static com.kolmir.identity_service.testutil.IdentityTestStringConstants.PASSWORD;
import static com.kolmir.identity_service.testutil.IdentityTestStringConstants.REALM;
import static com.kolmir.identity_service.testutil.IdentityTestStringConstants.SERVER_URL;
import static com.kolmir.identity_service.testutil.IdentityTestStringConstants.USER_CREATING_EXCEPTION_500;
import static com.kolmir.identity_service.testutil.IdentityTestStringConstants.USER_ROLE;
import static com.kolmir.identity_service.testutil.IdentityTestStringConstants.USERNAME;
import static com.kolmir.identity_service.testutil.IdentityTestStringConstants.USERNAME_NEW;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.RoleMappingResource;
import org.keycloak.admin.client.resource.RoleResource;
import org.keycloak.admin.client.resource.RoleScopeResource;
import org.keycloak.admin.client.resource.RolesResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kolmir.identity_service.dto.UserCreateRequest;
import com.kolmir.identity_service.exception.AlreadyExistsException;
import com.kolmir.identity_service.exception.CreatingException;
import com.kolmir.identity_service.model.User;
import com.kolmir.identity_service.util.KeycloakConstants;

import jakarta.ws.rs.core.Response;

@ExtendWith(MockitoExtension.class)
class UserAuthProviderImplTest {

    @Mock
    private Keycloak keycloak;

    @Mock
    private RealmResource realmResource;

    @Mock
    private UsersResource usersResource;

    @Mock
    private UserResource userResource;

    @Mock
    private RolesResource rolesResource;

    @Mock
    private RoleResource roleResource;

    @Mock
    private RoleMappingResource roleMappingResource;

    @Mock
    private RoleScopeResource roleScopeResource;

    @InjectMocks
    private UserAuthProviderImpl userAuthProvider;

    @BeforeEach
    void setUp() {
        userAuthProvider.realm = REALM;
        userAuthProvider.serverUrl = SERVER_URL;
        userAuthProvider.clientId = CLIENT_ID;
        userAuthProvider.clientSecret = CLIENT_SECRET;

        when(keycloak.realm(REALM)).thenReturn(realmResource);
        when(realmResource.users()).thenReturn(usersResource);
    }

    @Test
    @SuppressWarnings("unchecked")
    void createUser_shouldReturnIdAndAssignUserRoleWhenCreated() {
        UserCreateRequest request = userCreateRequest(EMAIL_NEW, USERNAME_NEW, PASSWORD, DISPLAY_NAME_NEW, BIO);
        Response response = org.mockito.Mockito.mock(Response.class);
        RoleRepresentation userRoleRepresentation = roleRepresentation(USER_ROLE);

        when(usersResource.create(any(UserRepresentation.class))).thenReturn(response);
        when(response.getStatus()).thenReturn(201);
        when(response.getHeaderString(KeycloakConstants.LOCATION_HEADER_NAME))
                .thenReturn(LOCATION_HEADER);

        when(realmResource.roles()).thenReturn(rolesResource);
        when(rolesResource.get(USER_ROLE)).thenReturn(roleResource);
        when(roleResource.toRepresentation()).thenReturn(userRoleRepresentation);
        when(usersResource.get(KEYCLOAK_ID_CREATED)).thenReturn(userResource);
        when(userResource.roles()).thenReturn(roleMappingResource);
        when(roleMappingResource.realmLevel()).thenReturn(roleScopeResource);

        String userId = userAuthProvider.createUser(request);

        assertThat(userId).isEqualTo(KEYCLOAK_ID_CREATED);
        ArgumentCaptor<List<RoleRepresentation>> rolesCaptor = ArgumentCaptor.forClass(List.class);
        verify(roleScopeResource).add(rolesCaptor.capture());
        assertThat(rolesCaptor.getValue()).containsExactly(userRoleRepresentation);
        verify(response).close();
    }

    @Test
    void createUser_shouldThrowAlreadyExistsExceptionWhenConflict() {
        UserCreateRequest request = userCreateRequest(EMAIL, USERNAME, PASSWORD, DISPLAY_NAME, null);
        Response response = org.mockito.Mockito.mock(Response.class);
        when(usersResource.create(any(UserRepresentation.class))).thenReturn(response);
        when(response.getStatus()).thenReturn(409);

        assertThatThrownBy(() -> userAuthProvider.createUser(request))
                .isInstanceOf(AlreadyExistsException.class)
                .hasMessage(KeycloakConstants.USER_ALREADY_EXISTS_MESSAGE);

        verify(response).close();
    }

    @Test
    void createUser_shouldThrowCreatingExceptionForUnexpectedStatus() {
        UserCreateRequest request = userCreateRequest(EMAIL, USERNAME, PASSWORD, DISPLAY_NAME, null);
        Response response = org.mockito.Mockito.mock(Response.class);
        when(usersResource.create(any(UserRepresentation.class))).thenReturn(response);
        when(response.getStatus()).thenReturn(500);

        assertThatThrownBy(() -> userAuthProvider.createUser(request))
                .isInstanceOf(CreatingException.class)
                .hasMessage(USER_CREATING_EXCEPTION_500);

        verify(response).close();
    }

    @Test
    void changeUserInfo_shouldUpdateUsernameAndEmail() {
        User user = user(null, KEYCLOAK_ID_ALT, EMAIL_NEW, USERNAME_NEW, null, null, true);

        UserRepresentation representation = userRepresentation(USERNAME, EMAIL, true);

        when(usersResource.get(KEYCLOAK_ID_ALT)).thenReturn(userResource);
        when(userResource.toRepresentation()).thenReturn(representation);

        userAuthProvider.changeUserInfo(user);

        assertThat(representation.getUsername()).isEqualTo(USERNAME_NEW);
        assertThat(representation.getEmail()).isEqualTo(EMAIL_NEW);
        verify(userResource).update(representation);
    }

    @Test
    void disableUser_shouldSetEnabledFalse() {
        UserRepresentation representation = userRepresentation(null, null, true);

        when(usersResource.get(KEYCLOAK_ID_ALT)).thenReturn(userResource);
        when(userResource.toRepresentation()).thenReturn(representation);

        userAuthProvider.disableUser(KEYCLOAK_ID_ALT);

        assertThat(representation.isEnabled()).isFalse();
        verify(userResource).update(representation);
    }
}
