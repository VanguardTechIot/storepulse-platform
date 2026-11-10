package com.vanguardtech.storepulse.platform.shared.domain.exceptions;

/**
 * Violation of a business rule. The message is the lowercase name of the error code, the same
 * key the web application translates (for example {@code unit_limit_exceeded}).
 */
public class BusinessRuleException extends RuntimeException {

    public BusinessRuleException(Enum<?> code) {
        super(code.name().toLowerCase());
    }
}