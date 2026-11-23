package cat.itacademy.s05.appointmentsapp.shared.adapter.in.web.security;

import jakarta.servlet.ServletException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;


class ProblemDetailAuthenticationEntryPointTest {

    private final JsonMapper jsonMapper = JsonMapper.builder().build();
    private final ProblemDetailAuthenticationEntryPoint authenticationEntryPoint = new ProblemDetailAuthenticationEntryPoint(jsonMapper);

    @Test
    void authenticationEntryPointShouldBeWrittenAsProblemDetail() throws IOException, ServletException {

        MockHttpServletRequest testRequest = new MockHttpServletRequest();
        MockHttpServletResponse testResponse = new MockHttpServletResponse();

        authenticationEntryPoint.commence(testRequest, testResponse, new UsernameNotFoundException("message"));

        assertThat(testResponse.getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
        assertThat(testResponse.getContentType()).isEqualTo(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
        assertThat(testResponse.getHeader(HttpHeaders.WWW_AUTHENTICATE)).startsWith("Bearer");

        JsonNode body = jsonMapper.readTree(testResponse.getContentAsString());
        assertThat(body.get("status").asInt()).isEqualTo(401);
        assertThat(body.get("title").asString()).isEqualTo("Unauthorized");
        assertThat(body.get("detail").asString()).isEqualTo("Authentication required");
    }
}
