package com.vanguardtech.storepulse.platform.profilespreferences.domain.model.valueobjects;

import com.vanguardtech.storepulse.platform.profilespreferences.domain.exceptions.ProfilesError;
import com.vanguardtech.storepulse.platform.shared.domain.exceptions.BusinessRuleException;
import jakarta.persistence.Embeddable;

import java.util.regex.Pattern;

/**
 * Contact phone of the profile, for example {@code +51 987 654 321}: an optional leading {@code +}
 * and 7 to 15 digits (the E.164 maximum), which may be separated by spaces, hyphens or parentheses.
 *
 * @param value the phone number, with consecutive whitespace reduced to a single space
 */
@Embeddable
public record PhoneNumber(String value) {

    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+?[\\d\\s()-]+$");
    private static final int MIN_DIGITS = 7;
    private static final int MAX_DIGITS = 15;

    /**
     * Normalizes and validates the number.
     *
     * @throws BusinessRuleException {@code invalid_phone_number} when the format or the digit count
     *                               is not supported
     */
    public PhoneNumber {
        if (value == null) throw new BusinessRuleException(ProfilesError.INVALID_PHONE_NUMBER);
        value = value.trim().replaceAll("\\s+", " ");
        var digits = value.replaceAll("\\D", "").length();
        if (!PHONE_PATTERN.matcher(value).matches() || digits < MIN_DIGITS || digits > MAX_DIGITS)
            throw new BusinessRuleException(ProfilesError.INVALID_PHONE_NUMBER);
    }

    /**
     * The phone is optional: an empty value removes it.
     *
     * @param value phone typed by the user, possibly empty
     * @return the phone number, or {@code null} when the value is empty
     */
    public static PhoneNumber ofNullable(String value) {
        return value == null || value.isBlank() ? null : new PhoneNumber(value);
    }
}
