package com.vanguardtech.storepulse.platform.iam.interfaces.rest.resources;

import com.vanguardtech.storepulse.platform.iam.domain.model.valueobjects.PasswordPolicy;
import com.vanguardtech.storepulse.platform.iam.domain.model.valueobjects.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Body of {@code POST /authentication/sign-up}. The password policy itself is checked by the domain
 * and answered as {@code weak_password}.
 *
 * @param fullName full name of the user
 * @param email    sign-in email
 * @param password plain password
 * @param role     {@code GALLERY_ADMINISTRATOR} when omitted; a tenant signs up with {@code TENANT}
 *                 before accepting its invitation
 */
public record SignUpResource(@NotBlank @Size(max = 150) String fullName,
                             @NotBlank @Email String email,
                             @NotBlank @Size(max = PasswordPolicy.MAXIMUM_LENGTH) String password,
                             Role role) {
}