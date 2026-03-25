package com.kolmir.identity_service.security;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;


@Component
public class KeycloakAuthoritiesConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    @Value("${spring.security.oauth2.resourceserver.jwt.clientid}")
    private String clientId;
    
    @Override
    @SuppressWarnings("unchecked")
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        Map<String, Object> realmAccess = jwt.getClaim("realm_access");
        List<String> realmRoles = realmAccess == null
                ? List.of()
                : (List<String>) realmAccess.getOrDefault("roles", List.of());
    
        Map<String, Object> resourceAccess = jwt.getClaim("resource_access");
        List<String> clientRoles = List.of();
    
        if (resourceAccess != null && resourceAccess.containsKey(clientId)) {
            Map<String, Object> client = (Map<String, Object>) resourceAccess.get(clientId);
            clientRoles = (List<String>) client.getOrDefault("roles", List.of());
        }
    
        return Stream.concat(
                realmRoles.stream()
                    .map(r -> new SimpleGrantedAuthority("ROLE_" + r))
                    .map(sga -> (GrantedAuthority)sga),
                clientRoles.stream()
                    .map(r -> new SimpleGrantedAuthority("ROLE_" + r))
                    .map(sga -> (GrantedAuthority)sga)
        ).distinct().toList();
        
    }
}
