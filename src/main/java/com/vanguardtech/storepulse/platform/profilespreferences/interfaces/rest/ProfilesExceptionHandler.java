package com.vanguardtech.storepulse.platform.profilespreferences.interfaces.rest;

import com.vanguardtech.storepulse.platform.profilespreferences.domain.exceptions.UnsupportedPhotoFormatException;
import com.vanguardtech.storepulse.platform.shared.interfaces.rest.GlobalExceptionHandler;
import com.vanguardtech.storepulse.platform.shared.interfaces.rest.resources.ErrorResource;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Answers an unsupported profile photo with {@code 415 Unsupported Media Type} (TS-04) instead of
 * the {@code 409 Conflict} that {@link GlobalExceptionHandler} gives to other business rule
 * violations. It runs first, so every other exception still reaches the global handler.
 */
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ProfilesExceptionHandler {

    /**
     * @param exception the rejected photo
     * @return a 415 body whose message is {@code unsupported_photo_format}
     */
    @ExceptionHandler(UnsupportedPhotoFormatException.class)
    public ResponseEntity<ErrorResource> handleUnsupportedPhotoFormat(UnsupportedPhotoFormatException exception) {
        var status = HttpStatus.UNSUPPORTED_MEDIA_TYPE;
        return ResponseEntity.status(status)
                .body(ErrorResource.of(status.value(), status.getReasonPhrase(), exception.getMessage()));
    }
}
