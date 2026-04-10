package com.kolmir.identity_service.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
        userAuthProvider.realm = "test-realm";
        userAuthProvider.serverUrl = "http://localhost:8080";
        userAuthProvider.clientId = "client";
        userAuthProvider.clientSecret = "secret";

        when(keycloak.realm("test-realm")).thenReturn(realmResource);
        when(realmResource.users()).thenReturn(usersResource);
    }

    @Test
    @SuppressWarnings("unchecked")
    void createUser_shouldReturnIdAndAssignUserRoleWhenCreated() {
        UserCreateRequest request = new UserCreateRequest("new@mail.com", "newuser", "pass1234", "New User", "bio");
        Response response = org.mockito.Mockito.mock(Response.class);
        RoleRepresentation roleRepresentation = new RoleRepresentation();
        roleRepresentation.setName("USER");

        when(usersResource.create(any(UserRepresentation.class))).thenReturn(response);
        when(response.getStatus()).thenReturn(201);
        when(response.getHeaderString(KeycloakConstants.LOCATION_HEADER_NAME))
                .thenReturn("http://localhost:8080/admin/realms/test/users/kc-11");

        when(realmResource.roles()).thenReturn(rolesResource);
        when(rolesResource.get("USER")).thenReturn(roleResource);
        when(roleResource.toRepresentation()).thenReturn(roleRepresentation);
        when(usersResource.get("kc-11")).thenReturn(userResource);
        when(userResource.roles()).thenReturn(roleMappingResource);
        when(roleMappingResource.realmLevel()).thenReturn(roleScopeResource);

        String userId = userAuthProvider.createUser(request);

        assertThat(userId).isEqualTo("kc-11");
        ArgumentCaptor<List<RoleRepresentation>> rolesCaptor = ArgumentCaptor.forClass(List.class);
        verify(roleScopeResource).add(rolesCaptor.capture());
        assertThat(rolesCaptor.getValue()).containsExactly(roleRepresentation);
        verify(response).close();
    }

    @Test
    void createUser_shouldThrowAlreadyExistsExceptionWhenConflict() {
        UserCreateRequest request = new UserCreateRequest("dup@mail.com", "dup", "pass1234", "Dup", null);
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
        UserCreateRequest request = new UserCreateRequest("u@mail.com", "u", "pass1234", "U", null);
        Response response = org.mockito.Mockito.mock(Response.class);
        when(usersResource.create(any(UserRepresentation.class))).thenReturn(response);
        when(response.getStatus()).thenReturn(500);

        assertThatThrownBy(() -> userAuthProvider.createUser(request))
                .isInstanceOf(CreatingException.class)
                .hasMessage("User creating exception with status 500");

        verify(response).close();
    }

    @Test
    void changeUserInfo_shouldUpdateUsernameAndEmail() {
        User user = new User();
        user.setKeycloakId("kc-22");
        user.setUsername("new_name");
        user.setEmail("new@mail.com");

        UserRepresentation representation = new UserRepresentation();
        representation.setUsername("old_name");
        representation.setEmail("old@mail.com");

        when(usersResource.get("kc-22")).thenReturn(userResource);
        when(userResource.toRepresentation()).thenReturn(representation);

        userAuthProvider.changeUserInfo(user);

        assertThat(representation.getUsername()).isEqualTo("new_name");
        assertThat(representation.getEmail()).isEqualTo("new@mail.com");
        verify(userResource).update(representation);
    }

    @Test
    void disableUser_shouldSetEnabledFalse() {
        UserRepresentation representation = new UserRepresentation();
        representation.setEnabled(true);

        when(usersResource.get("kc-30")).thenReturn(userResource);
        when(userResource.toRepresentation()).thenReturn(representation);

        userAuthProvider.disableUser("kc-30");

        assertThat(representation.isEnabled()).isFalse();
        verify(userResource).update(representation);
    }
}
