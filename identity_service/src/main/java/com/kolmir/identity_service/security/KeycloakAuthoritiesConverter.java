package com.kolmir.identity_service.security;

import java.util.Collection;
import java.util.List;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import static com.kolmir.identity_service.util.KeycloakConstants.*;


public class KeycloakAuthoritiesConverter implements Converter<Jwt, AbstractAuthenticationToken> {
    
    @SuppressWarnings("unchecked")
    public Collection<GrantedAuthority> getAuthorities(Jwt jwt) {
        var realmAccess = jwt.getClaimAsMap(REALM_ACCESS_NAME);
        var realmRoles = (List<String>)realmAccess.get(ROLES_LIST_NAME);

        return realmRoles.stream()
                .map(role -> "ROLE_" + role)
                .map(SimpleGrantedAuthority::new)
                .map(GrantedAuthority.class::cast)
                .toList();
    }

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        String principalName = jwt.getClaimAsString(CLAIM_NAME);
        return new JwtAuthenticationToken(jwt, getAuthorities(jwt), principalName);
    }

    
}
