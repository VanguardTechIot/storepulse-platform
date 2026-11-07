package com.vanguardtech.storepulse.platform.iam.interfaces.rest.resources;

import com.vanguardtech.storepulse.platform.iam.domain.model.valueobjects.PasswordPolicy;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Body of {@code POST /password-recoveries/reset}.
 *
 * @param email       email of the account
 * @param code        six-digit recovery code received by email
 * @param newPassword plain new password
 */
public record ResetPasswordResource(@NotBlank @Email String email,
                                    @NotBlank String code,
                                    @NotBlank @Size(max = PasswordPolicy.MAXIMUM_LENGTH) String newPassword) {
}
