package ru.butenko.config;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SecurityConfigTest {

    private final SecurityConfig securityConfig = new SecurityConfig();

    @Test
    void keycloakRolesConverter_shouldMapRealmRolesToSpringAuthorities() {
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .subject("11111111-1111-1111-1111-111111111111")
                .claim("realm_access", Map.of("roles", java.util.List.of("USER", "MANAGER", "WAREHOUSE_ADMIN", "ADMIN")))
                .build();

        Collection<GrantedAuthority> authorities = securityConfig.keycloakRolesConverter().convert(jwt);

        Assertions.assertNotNull(authorities);
        Set<String> actual = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        Set<String> expected = Set.of(
                "ROLE_USER",
                "ROLE_MANAGER",
                "ROLE_WAREHOUSE_ADMIN",
                "ROLE_ADMIN"
        );

        assertEquals(expected, actual);
    }

    @Test
    void keycloakRolesConverter_shouldReturnEmptyCollection_whenRealmAccessIsMissing() {
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .subject("11111111-1111-1111-1111-111111111111")
                .build();

        Collection<GrantedAuthority> authorities = securityConfig.keycloakRolesConverter().convert(jwt);

        Assertions.assertNotNull(authorities);
        assertTrue(authorities.isEmpty());
    }
}
