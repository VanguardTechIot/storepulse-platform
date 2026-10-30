package com.vanguardtech.storepulse.platform.iam.interfaces.rest.resources;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Body of {@code POST /password-recoveries}.
 *
 * @param email email of the account whose password is forgotten
 */
public record RequestPasswordResetResource(@NotBlank @Email String email) {
}
