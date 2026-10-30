package com.vanguardtech.storepulse.platform.iam.domain.model.valueobjects;

import java.util.regex.Pattern;

/**
 * Minimum password complexity required to register or change a password (US-01, US-03): at least
 * 8 characters and at least one number. It is the same policy the web application checks while the
 * user types.
 */
public final class PasswordPolicy {

    /** Minimum number of characters required in a password. */
    public static final int MINIMUM_LENGTH = 8;

    /** Maximum number of characters accepted; the hashing algorithm ignores anything longer. */
    public static final int MAXIMUM_LENGTH = 72;

    private static final Pattern NUMBER = Pattern.compile("\\d");

    private PasswordPolicy() {
    }

    /**
     * Tells whether a password satisfies every requirement of the policy.
     *
     * @param password plain password to evaluate
     * @return {@code true} when it has between {@value #MINIMUM_LENGTH} and {@value #MAXIMUM_LENGTH}
     * characters and contains at least one number
     */
    public static boolean isSatisfiedBy(String password) {
        return password != null
                && password.length() >= MINIMUM_LENGTH
                && password.length() <= MAXIMUM_LENGTH
                && NUMBER.matcher(password).find();
    }
}
