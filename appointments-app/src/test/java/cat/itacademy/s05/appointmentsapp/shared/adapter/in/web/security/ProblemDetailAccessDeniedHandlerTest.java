package cat.itacademy.s05.appointmentsapp.shared.adapter.in.web.security;


import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import static org.assertj.core.api.Assertions.assertThat;

class ProblemDetailAccessDeniedHandlerTest {

    private final JsonMapper jsonMapper = JsonMapper.builder().build();
    private final ProblemDetailAccessDeniedHandler handler = new ProblemDetailAccessDeniedHandler(jsonMapper);

    @Test
    void accessDeniedShouldBeWrittenAsProblemDetail() throws Exception {
        MockHttpServletRequest testRequest = new MockHttpServletRequest();
        MockHttpServletResponse testResponse = new MockHttpServletResponse();

        handler.handle(testRequest, testResponse, new AccessDeniedException("message"));

        assertThat(testResponse.getStatus()).isEqualTo(HttpStatus.FORBIDDEN.value());
        assertThat(testResponse.getContentType()).isEqualTo(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
        assertThat(testResponse.getHeader(HttpHeaders.WWW_AUTHENTICATE)).startsWith("Bearer");

        JsonNode body = jsonMapper.readTree(testResponse.getContentAsString());
        assertThat(body.get("status").asInt()).isEqualTo(403);
        assertThat(body.get("title").asString()).isEqualTo("Forbidden");
        assertThat(body.get("detail").asString()).isEqualTo("Permission denied");
    }
}
