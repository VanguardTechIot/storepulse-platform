package com.vanguardtech.storepulse.platform.profilespreferences.domain.exceptions;

import com.vanguardtech.storepulse.platform.shared.domain.exceptions.BusinessRuleException;

/**
 * The profile photo is not a supported image. Unlike other business rule violations it is answered
 * with {@code 415 Unsupported Media Type} (TS-04).
 */
public class UnsupportedPhotoFormatException extends BusinessRuleException {

    /** Creates the exception with the {@code unsupported_photo_format} code. */
    public UnsupportedPhotoFormatException() {
        super(ProfilesError.UNSUPPORTED_PHOTO_FORMAT);
    }
}
