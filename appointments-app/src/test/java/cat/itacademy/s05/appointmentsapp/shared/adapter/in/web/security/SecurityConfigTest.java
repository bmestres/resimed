package cat.itacademy.s05.appointmentsapp.shared.adapter.in.web.security;

import cat.itacademy.s05.appointmentsapp.shared.domain.Role;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.startsWith;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@Import(SecurityConfig.class)
@ActiveProfiles("test")
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void requestWithoutTokenShouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/any/path"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void requestWithInvalidTokenShouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/any/path").header(HttpHeaders.AUTHORIZATION, "Bearer not-a-jwt"))
                .andExpect(status().isUnauthorized());
    }

    @ParameterizedTest
    @EnumSource(Role.class)
    void requestWithUserOfAnyRoleShouldBeForbidden(Role role) throws Exception {
        mockMvc.perform(get("/any/path").with(user("user").roles(role.name())))
                .andExpect(status().isForbidden());
    }

    @Test
    void responseShouldChallengeForBearerNotBasic() throws Exception {
        mockMvc.perform(get("/any/path"))
                .andExpect(header().string(HttpHeaders.WWW_AUTHENTICATE, startsWith("Bearer")));
    }
}
