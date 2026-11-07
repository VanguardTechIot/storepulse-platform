package com.vanguardtech.storepulse.platform.propertymanagement.domain.model.valueobjects;

import com.vanguardtech.storepulse.platform.propertymanagement.domain.exceptions.PropertyManagementError;
import com.vanguardtech.storepulse.platform.shared.domain.exceptions.BusinessRuleException;
import jakarta.persistence.Embeddable;
import java.util.regex.Pattern;

/** Email the invitation is sent to, normalized to lower case. */
@Embeddable
public record InvitationEmail(String address) {

    private static final Pattern PATTERN = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");

    public InvitationEmail {
        if (address == null || !PATTERN.matcher(normalize(address)).matches())
            throw new BusinessRuleException(PropertyManagementError.INVALID_EMAIL);
        address = normalize(address);
    }

    public static String normalize(String address) {
        return address.trim().toLowerCase();
    }
}
