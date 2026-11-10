package com.vanguardtech.storepulse.platform.profilespreferences.domain.model.valueobjects;

import com.vanguardtech.storepulse.platform.profilespreferences.domain.exceptions.UnsupportedPhotoFormatException;
import jakarta.persistence.Embeddable;

import java.net.URI;
import java.util.List;
import java.util.Locale;

/**
 * Profile photo, stored as a link to the image. Only JPG and PNG images served over HTTP(S) are
 * supported; any other format is answered with 415.
 *
 * @param url the trimmed link to the image
 */
@Embeddable
public record ProfilePhoto(String url) {

    private static final List<String> SUPPORTED_SCHEMES = List.of("http", "https");
    private static final List<String> SUPPORTED_EXTENSIONS = List.of(".jpg", ".jpeg", ".png");

    /**
     * Validates the link.
     *
     * @throws UnsupportedPhotoFormatException when it is not a JPG or PNG image served over HTTP(S)
     */
    public ProfilePhoto {
        if (url == null) throw new UnsupportedPhotoFormatException();
        url = url.trim();
        if (!isSupported(url)) throw new UnsupportedPhotoFormatException();
    }

    /**
     * The photo is optional: an empty value removes it, so the default image is shown again.
     *
     * @param url link typed by the user, possibly empty
     * @return the photo, or {@code null} when the link is empty
     */
    public static ProfilePhoto ofNullable(String url) {
        return url == null || url.isBlank() ? null : new ProfilePhoto(url);
    }

    private static boolean isSupported(String url) {
        try {
            var uri = URI.create(url);
            var scheme = uri.getScheme();
            var path = uri.getPath();
            return scheme != null && uri.getHost() != null && path != null
                    && SUPPORTED_SCHEMES.contains(scheme.toLowerCase(Locale.ROOT))
                    && SUPPORTED_EXTENSIONS.stream().anyMatch(path.toLowerCase(Locale.ROOT)::endsWith);
        } catch (IllegalArgumentException exception) {
            return false;
        }
    }
}