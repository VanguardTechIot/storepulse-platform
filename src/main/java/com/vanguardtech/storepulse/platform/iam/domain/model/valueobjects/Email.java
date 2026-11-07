package com.vanguardtech.storepulse.platform.iam.domain.model.valueobjects;

import jakarta.persistence.Embeddable;

import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Email address used as the sign-in identifier.
 *
 * <p>The address is normalized (trimmed and lowercased) before it is validated and stored, so two
 * spellings of the same address are the same value.
 *
 * @param address the normalized email address
 */
@Embeddable
public record Email(String address) {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");

    /**
     * Normalizes and validates the address.
     *
     * @throws IllegalArgumentException when the address is missing or malformed
     */
    public Email {
        if (address == null || address.isBlank()) throw new IllegalArgumentException("Email is required");
        address = normalize(address);
        if (!EMAIL_PATTERN.matcher(address).matches())
            throw new IllegalArgumentException("Invalid email: " + address);
    }

    /**
     * Trims whitespace and converts an email address to lowercase.
     *
     * @param address email address to normalize
     * @return the normalized address
     */
    public static String normalize(String address) {
        return address.trim().toLowerCase(Locale.ROOT);
    }
}
