package com.kolmir.identity_service.service.impl;

import java.util.List;
import java.util.Map;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.kolmir.identity_service.dto.UserRegisterRequest;
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
    @Value("${keycloak.server-url}") 
    public String serverUrl;

    @Value("${keycloak.realm}") 
    public String realm;

    @Value("${keycloak.client-id}")
    public String clientId;

    @Value("${keycloak.client-secret}")
    public String clientSecret;

    private final Keycloak keycloak;
    private final RestTemplate restTemplate = new RestTemplate();
    
    @Override
    public String createUser(UserRegisterRequest request) {
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
        UserResource userResource = keycloak.realm(realm)
        .users()
        .get(user.getKeycloakId());
        UserRepresentation userRepresentation = userResource.toRepresentation();
        
        userRepresentation.setUsername(user.getUsername());
        userRepresentation.setEmail(user.getEmail());

        userResource.update(userRepresentation);
    }
    
    @Override
    public void disableUser(String userId) {
        UserResource userResource = keycloak.realm(realm).users()
        .get(userId);
        UserRepresentation userRepresentation = userResource.toRepresentation();
        
        userRepresentation.setEnabled(false);
        userResource.update(userRepresentation);
        userResource.logout();
        
    }
    
    @Override
    public Map<String, Object> getTokensForUser(String username, String password) {
        MultiValueMap<String, String> grants = new LinkedMultiValueMap<>();
        grants.add("username", username);
        grants.add("password", password);
        return takeToken("password", grants);
    }
    
    @Override
    public Map<String, Object> refreshUserToken(String refreshToken) {
        MultiValueMap<String, String> grants = new LinkedMultiValueMap<>();
        grants.add("refresh_token", refreshToken);
        return takeToken("refresh_token", grants);
    }
    
    @Override
    public void deleteUser(String userId) {
        keycloak.realm(realm)
            .users()
            .delete(userId);
    }

    private Map<String, Object> takeToken(String grantType, MultiValueMap<String, String> grants) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", grantType);
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.addAll(grants);
        
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
        
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
            getTokenUrl(),
            HttpMethod.POST,
            request,
            new ParameterizedTypeReference<Map<String, Object>>() {}
        );
        
        return response.getBody();
    }

    private UserRepresentation getRepresentation(UserRegisterRequest request) {
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

    @Override
    public void changeUserRole(String userId, String oldRole, String newRole) {
        try {
            deleteUserRole(userId, oldRole);
            setUserRole(userId, newRole);
        } catch (Exception e) {
            deleteUserRole(userId, newRole);
            setUserRole(userId, oldRole);
            throw e;
        }
    }

    private void deleteUserRole(String userId, String role) {
        RoleRepresentation roleRepresentation = keycloak.realm(realm)
                                                    .roles()
                                                    .get(role)
                                                    .toRepresentation();

        keycloak.realm(realm)
            .users()
            .get(userId)
            .roles()
            .realmLevel()
            .remove(List.of(roleRepresentation));
    }
        
    private String getTokenUrl() {
        return String.format("%s/realms/%s/protocol/openid-connect/token", serverUrl, realm);
    }

}
                                        
