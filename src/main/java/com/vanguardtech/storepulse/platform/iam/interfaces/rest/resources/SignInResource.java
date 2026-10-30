package com.vanguardtech.storepulse.platform.iam.interfaces.rest.resources;

import com.vanguardtech.storepulse.platform.iam.domain.model.valueobjects.PasswordPolicy;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Body of {@code POST /authentication/sign-in}.
 *
 * @param email    sign-in email
 * @param password plain password
 */
public record SignInResource(@NotBlank String email,
                             @NotBlank @Size(max = PasswordPolicy.MAXIMUM_LENGTH) String password) {
}
