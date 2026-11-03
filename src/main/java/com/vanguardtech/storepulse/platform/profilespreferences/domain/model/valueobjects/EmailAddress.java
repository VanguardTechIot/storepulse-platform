package com.vanguardtech.storepulse.platform.profilespreferences.domain.model.valueobjects;

import com.vanguardtech.storepulse.platform.profilespreferences.domain.exceptions.ProfilesError;
import com.vanguardtech.storepulse.platform.shared.domain.exceptions.BusinessRuleException;
import jakarta.persistence.Embeddable;

import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Email of the profile. It is also the sign-in email, so it follows the same rule as Identity and
 * Access Management: always stored trimmed and in lower case.
 *
 * @param value the normalized email address
 */
@Embeddable
public record EmailAddress(String value) {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");

    /**
     * Normalizes and validates the address.
     *
     * @throws BusinessRuleException {@code invalid_email} when the address is missing or malformed
     */
    public EmailAddress {
        if (value == null) throw new BusinessRuleException(ProfilesError.INVALID_EMAIL);
        value = value.trim().toLowerCase(Locale.ROOT);
        if (!EMAIL_PATTERN.matcher(value).matches()) throw new BusinessRuleException(ProfilesError.INVALID_EMAIL);
    }
}
