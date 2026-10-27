package com.vanguardtech.storepulse.platform.shared.interfaces.rest;


import com.vanguardtech.storepulse.platform.shared.interfaces.rest.resources.ErrorResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/**
 * Translates exceptions into {@link ErrorResource} bodies, so every Bounded Context answers
 * errors in the same shape.
 *
 * <p>Domain rules signal a violation with {@link IllegalArgumentException}, for example a value
 * object that rejects a malformed email.
 */

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResource> handleDomainRule(IllegalArgumentException exception) {
        return build(exception.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResource> handleInvalidBody(MethodArgumentNotValidException exception) {
        var message = exception.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return build(message);
    }

    private ResponseEntity<ErrorResource> build(String message) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResource.of(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), message));
    }
}
