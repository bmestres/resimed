package cat.itacademy.s05.appointmentsapp.shared.adapter.in.web.security;

import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtDecoderTest {

    private static final String FIRST_SECRET = "test-only-not-a-real-secret-9876543210";
    private static final String SECOND_SECRET = "another-test-not-a-real-secret-123456789";

    private final JwtDecoder decoder = new SecurityConfig().jwtDecoder(new JwtProperties(FIRST_SECRET));

    @Test
    void validTokenShouldBeDecoded() {
       String token = tokenSignedWith(FIRST_SECRET, Instant.now().plus(Duration.ofHours(1)));

        Jwt jwt = decoder.decode(token);

        assertThat(jwt.getSubject()).isEqualTo("user-id");
        assertThat(jwt.getClaimAsString("role")).isEqualTo("STAFF");
    }

    @Test
    void tokenSignedWithAnotherSecretShouldBeRejected() {
        String token = tokenSignedWith(SECOND_SECRET, Instant.now().plus(Duration.ofHours(1)));

        assertThatThrownBy(() -> decoder.decode(token))
                .isInstanceOf(JwtException.class)
                .hasMessageContaining("Invalid signature");
    }

    @Test
    void expiredTokenShouldBeRejected() {
        String token = tokenSignedWith(FIRST_SECRET, Instant.now().minus(Duration.ofMinutes(5)));

        assertThatThrownBy(() -> decoder.decode(token))
                .isInstanceOf(JwtException.class)
                .hasMessageContaining("expired");
    }

private static String tokenSignedWith(String secret, Instant expiration) {
    SecretKeySpec key = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
    NimbusJwtEncoder encoder = NimbusJwtEncoder.withSecretKey(key).build();

    JwtClaimsSet claims = JwtClaimsSet.builder()
            .subject("user-id")
            .claim("role", "STAFF")
            .issuedAt(expiration.minus(Duration.ofHours(1)))
            .expiresAt(expiration)
            .build();

    JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();

    return encoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }
}
