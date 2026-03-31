package com.kolmir.identity_service.service.impl;

import java.util.List;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.kolmir.identity_service.dto.UserCreateRequest;
import com.kolmir.identity_service.exception.AlreadyExistsException;
import com.kolmir.identity_service.exception.CreatingException;
import com.kolmir.identity_service.model.User;
import com.kolmir.identity_service.model.UserRole;
import com.kolmir.identity_service.service.UserAuthProvider;
import static com.kolmir.identity_service.util.KeycloakConstants.*;

import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class UserAuthProviderImpl implements UserAuthProvider {

    private final Keycloak keycloak;

    @Value("${keycloak.realm}")
    private String realm;

    @Override
    public String createUser(UserCreateRequest request) {
        UserRepresentation user = getRepresentation(request);
        UsersResource usersResourse = keycloak.realm(realm).users();

        try (Response response = usersResourse.create(user)) {
            if (response.getStatus() == HttpStatus.CREATED.value()) {
                String location = response.getHeaderString(LOCATION_HEADER_NAME);
                String userId = location.substring(location.lastIndexOf("/") + 1);
                setUserRole(userId, UserRole.USER.getStringName());
                return userId;
            }
            if (response.getStatus() == HttpStatus.CONFLICT.value())
                throw new AlreadyExistsException(USER_ALREADY_EXISTS_MESSAGE);

            throw new CreatingException(String.format(USER_CREATING_EXCEPTION_TEMPLATE, response.getStatus()));
        }
    }

    @Override
    public void changeUserInfo(User user) {
        UserResource userResource = keycloak.realm(realm).users()
                                    .get(user.getKeycloakId().toString());
        UserRepresentation userRepresentation = userResource.toRepresentation();

        userRepresentation.setUsername(user.getUsername());
        userRepresentation.setEmail(user.getEmail());

        userResource.update(userRepresentation);
    }

    @Override
    public void disableUser(String userId) {
        UserResource userResource = keycloak.realm(realm).users()
                                    .get(userId.toString());
        UserRepresentation userRepresentation = userResource.toRepresentation();

        userRepresentation.setEnabled(false);
        userResource.update(userRepresentation);
    }

    private UserRepresentation getRepresentation(UserCreateRequest request) {
        UserRepresentation user = new UserRepresentation();

        user.setEnabled(true);
        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setEmailVerified(true);

        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(request.password());
        credential.setTemporary(false);

        user.setCredentials(List.of(credential));

        return user;
    }

    private void setUserRole(String userId, String role) {
        RoleRepresentation roleRepresentation = keycloak.realm(realm)
                                                .roles()
                                                .get(role)
                                                .toRepresentation();

        keycloak.realm(realm)
        .users()
        .get(userId)
        .roles()
        .realmLevel()
        .add(List.of(roleRepresentation));
    }
}
