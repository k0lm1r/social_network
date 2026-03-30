package com.kolmir.identity_service.security;

import java.util.Collection;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import static com.kolmir.identity_service.util.KeycloakConstants.*;


public class KeycloakAuthoritiesConverter implements Converter<Jwt, Collection<GrantedAuthority>> {
    
    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        var realmRoles = jwt.getClaimAsStringList(ROLES_LIST_NAME);

        return realmRoles.stream()
                .map(role -> "ROLE_" + role)
                .map(SimpleGrantedAuthority::new)
                .map(GrantedAuthority.class::cast)
                .toList();
    }
}
