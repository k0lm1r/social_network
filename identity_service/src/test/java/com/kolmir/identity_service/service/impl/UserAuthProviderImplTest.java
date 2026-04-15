package com.kolmir.identity_service.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static com.kolmir.identity_service.testutil.IdentityServiceTestObjectFactory.roleRepresentation;
import static com.kolmir.identity_service.testutil.IdentityServiceTestObjectFactory.user;
import static com.kolmir.identity_service.testutil.IdentityServiceTestObjectFactory.userCreateRequest;
import static com.kolmir.identity_service.testutil.IdentityServiceTestObjectFactory.userRepresentation;
import static com.kolmir.identity_service.testutil.IdentityServiceTestConstants.BIO;
import static com.kolmir.identity_service.testutil.IdentityServiceTestConstants.CLIENT_ID;
import static com.kolmir.identity_service.testutil.IdentityServiceTestConstants.CLIENT_SECRET;
import static com.kolmir.identity_service.testutil.IdentityServiceTestConstants.DISPLAY_NAME;
import static com.kolmir.identity_service.testutil.IdentityServiceTestConstants.DISPLAY_NAME_NEW;
import static com.kolmir.identity_service.testutil.IdentityServiceTestConstants.EMAIL;
import static com.kolmir.identity_service.testutil.IdentityServiceTestConstants.EMAIL_NEW;
import static com.kolmir.identity_service.testutil.IdentityServiceTestConstants.KEYCLOAK_ID_ALT;
import static com.kolmir.identity_service.testutil.IdentityServiceTestConstants.KEYCLOAK_ID_CREATED;
import static com.kolmir.identity_service.testutil.IdentityServiceTestConstants.LOCATION_HEADER;
import static com.kolmir.identity_service.testutil.IdentityServiceTestConstants.PASSWORD;
import static com.kolmir.identity_service.testutil.IdentityServiceTestConstants.REALM;
import static com.kolmir.identity_service.testutil.IdentityServiceTestConstants.SERVER_URL;
import static com.kolmir.identity_service.testutil.IdentityServiceTestConstants.USER_CREATING_EXCEPTION_500;
import static com.kolmir.identity_service.testutil.IdentityServiceTestConstants.USER_ROLE;
import static com.kolmir.identity_service.testutil.IdentityServiceTestConstants.USERNAME;
import static com.kolmir.identity_service.testutil.IdentityServiceTestConstants.USERNAME_NEW;
import static com.kolmir.identity_service.testutil.IdentityServiceTestConstants.HTTP_CONFLICT;
import static com.kolmir.identity_service.testutil.IdentityServiceTestConstants.HTTP_CREATED;
import static com.kolmir.identity_service.testutil.IdentityServiceTestConstants.HTTP_INTERNAL_SERVER_ERROR;
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

import com.kolmir.identity_service.dto.UserRegisterRequest;
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
        UserRegisterRequest request = userCreateRequest(EMAIL_NEW, USERNAME_NEW, PASSWORD, DISPLAY_NAME_NEW, BIO);
        Response response = org.mockito.Mockito.mock(Response.class);
        RoleRepresentation userRoleRepresentation = roleRepresentation(USER_ROLE);

        when(usersResource.create(any(UserRepresentation.class))).thenReturn(response);
        when(response.getStatus()).thenReturn(HTTP_CREATED);
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
        UserRegisterRequest request = userCreateRequest(EMAIL, USERNAME, PASSWORD, DISPLAY_NAME, null);
        Response response = org.mockito.Mockito.mock(Response.class);
        when(usersResource.create(any(UserRepresentation.class))).thenReturn(response);
        when(response.getStatus()).thenReturn(HTTP_CONFLICT);

        assertThatThrownBy(() -> userAuthProvider.createUser(request))
                .isInstanceOf(AlreadyExistsException.class)
                .hasMessage(KeycloakConstants.USER_ALREADY_EXISTS_MESSAGE);

        verify(response).close();
    }

    @Test
    void createUser_shouldThrowCreatingExceptionForUnexpectedStatus() {
        UserRegisterRequest request = userCreateRequest(EMAIL, USERNAME, PASSWORD, DISPLAY_NAME, null);
        Response response = org.mockito.Mockito.mock(Response.class);
        when(usersResource.create(any(UserRepresentation.class))).thenReturn(response);
        when(response.getStatus()).thenReturn(HTTP_INTERNAL_SERVER_ERROR);

        assertThatThrownBy(() -> userAuthProvider.createUser(request))
                .isInstanceOf(CreatingException.class)
                .hasMessage(USER_CREATING_EXCEPTION_500);

        verify(response).close();
    }

    @Test
    void changeUserInfo_shouldUpdateUsernameAndEmail() {
        User user = user(null, KEYCLOAK_ID_ALT, EMAIL_NEW, USERNAME_NEW, null, null, true);

        UserRepresentation representation = userRepresentation(USERNAME, EMAIL, true);
        UserRepresentation currentStateRepresentation = userRepresentation(USERNAME, EMAIL, true);

        when(usersResource.get(KEYCLOAK_ID_ALT)).thenReturn(userResource);
        when(userResource.toRepresentation()).thenReturn(representation, currentStateRepresentation);

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
