package com.vanguardtech.storepulse.platform.shared.domain.exceptions;

/** The requested aggregate does not exist. */
public class ResourceNotFoundException extends BusinessRuleException {

    public ResourceNotFoundException(Enum<?> code) {
        super(code);
    }
}