package cat.itacademy.s05.appointmentsapp.shared.adapter.in.web.security;

import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;

import static org.assertj.core.api.Assertions.assertThat;

public class JwtAuthenticationConverterTest {

    private final JwtAuthenticationConverter testConverter = new SecurityConfig().jwtAuthenticationConverter();

    @Test
    void roleClaimShouldMapToRoleAuthority() {
        Jwt jwt = Jwt.withTokenValue("token")
                .header("algorithm", "HS256")
                .subject("user-id")
                .claim("role", "STAFF")
                .build();

        AbstractAuthenticationToken authenticationToken = testConverter.convert(jwt);

        assertThat(authenticationToken.getAuthorities())
                .extracting(grantedAuthority-> grantedAuthority.getAuthority())
                .filteredOn(auth-> auth.startsWith("ROLE_"))
                .containsExactly("ROLE_STAFF");
    }

    @Test
    void tokenWithoutRoleShouldNotHaveRoleAuthorities() {
        Jwt jwt = Jwt.withTokenValue("token")
                .header("algorithm", "HS256")
                .subject("user-id")
                .claim("scope", "read")
                .build();

        AbstractAuthenticationToken authenticationToken = testConverter.convert(jwt);

        assertThat(authenticationToken.getAuthorities())
                .extracting(grantedAuthority-> grantedAuthority.getAuthority())
                .filteredOn(auth-> auth.startsWith("ROLE_"))
                .isEmpty();
    }
}
