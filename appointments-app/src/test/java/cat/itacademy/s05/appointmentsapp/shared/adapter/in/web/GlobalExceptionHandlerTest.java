package cat.itacademy.s05.appointmentsapp.shared.adapter.in.web;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.InsufficientAuthenticationException;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler();

    @Test
    void authenticationExceptionShouldMapTo401() {

        ProblemDetail problemDetail = exceptionHandler.handleAuthentication(new InsufficientAuthenticationException("message"));

        assertThat(problemDetail.getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
        assertThat(problemDetail.getDetail()).isEqualTo("Authentication required");
    }

    @Test
    void accessDeniedExceptionShouldMapTo403() {

        ProblemDetail problemDetail = exceptionHandler.handleAccessDenied(new AccessDeniedException("message"));

        assertThat(problemDetail.getStatus()).isEqualTo(HttpStatus.FORBIDDEN.value());
        assertThat(problemDetail.getDetail()).isEqualTo("Permission denied");
    }

    @Test
    void unexpectedExceptionMapTo500() {

        ProblemDetail problemDetail = exceptionHandler.handleUnexpected(new RuntimeException("message"));

        assertThat(problemDetail.getStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
        assertThat(problemDetail.getDetail()).isEqualTo("Unexpected error");
    }
}