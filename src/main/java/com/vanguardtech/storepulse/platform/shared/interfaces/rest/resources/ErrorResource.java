package com.vanguardtech.storepulse.platform.shared.interfaces.rest.resources;

import java.time.Instant;

/** Uniform body of every error answered by the REST API. */
public record ErrorResource(int status, String error, String message, Instant timestamp) {

    public static ErrorResource of(int status, String error, String message){
        return new ErrorResource(status, error, message, Instant.now());
    }
}
