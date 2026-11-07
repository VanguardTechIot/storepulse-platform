package com.vanguardtech.storepulse.platform.iam.domain.model.valueobjects;

import java.security.SecureRandom;
import java.util.regex.Pattern;

/**
 * 6-digit code that confirms a password recovery (US-03). Only its hash is persisted; the plain
 * code exists just long enough to be delivered to the user.
 *
 * @param value the six digits of the code
 */
public record PasswordResetCode(String value) {

    /** Number of digits of a code. */
    public static final int LENGTH = 6;

    private static final Pattern CODE_PATTERN = Pattern.compile("^\\d{" + LENGTH + "}$");
    private static final SecureRandom RANDOM = new SecureRandom();

    /**
     * Validates the code.
     *
     * @throws IllegalArgumentException when the value does not have exactly six digits
     */
    public PasswordResetCode {
        if (!isValid(value)) throw new IllegalArgumentException("A password reset code must have exactly 6 digits");
    }

    /**
     * Generates a new code with a cryptographically strong random number generator.
     *
     * @return a new six-digit code
     */
    public static PasswordResetCode generate() {
        return new PasswordResetCode("%06d".formatted(RANDOM.nextInt(1_000_000)));
    }

    /**
     * Tells whether a value has the shape of a code.
     *
     * @param value candidate code
     * @return {@code true} when it has exactly six digits
     */
    public static boolean isValid(String value) {
        return value != null && CODE_PATTERN.matcher(value).matches();
    }

    /** Hides the code so it never ends up in logs. */
    @Override
    public String toString() {
        return "PasswordResetCode[******]";
    }
}
