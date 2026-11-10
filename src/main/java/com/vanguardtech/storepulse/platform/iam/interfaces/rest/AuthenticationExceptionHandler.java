package com.vanguardtech.storepulse.platform.iam.interfaces.rest;

import com.vanguardtech.storepulse.platform.iam.domain.exceptions.AuthenticationFailedException;
import com.vanguardtech.storepulse.platform.shared.interfaces.rest.GlobalExceptionHandler;
import com.vanguardtech.storepulse.platform.shared.interfaces.rest.resources.ErrorResource;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Answers a failed authentication with {@code 401 Unauthorized} instead of the {@code 409 Conflict}
 * that {@link GlobalExceptionHandler} gives to other business rule violations. It runs first, so
 * every other exception still reaches the global handler.
 */
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class AuthenticationExceptionHandler {

    /**
     * @param exception the rejected authentication
     * @return a 401 body whose message is the error code, for example {@code invalid_credentials}
     */
    @ExceptionHandler(AuthenticationFailedException.class)
    public ResponseEntity<ErrorResource> handleAuthenticationFailed(AuthenticationFailedException exception) {
        var status = HttpStatus.UNAUTHORIZED;
        return ResponseEntity.status(status)
                .body(ErrorResource.of(status.value(), status.getReasonPhrase(), exception.getMessage()));
    }
}
