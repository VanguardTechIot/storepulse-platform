package com.vanguardtech.storepulse.platform.propertycommunication.domain.model.valueobjects;

import com.vanguardtech.storepulse.platform.propertycommunication.domain.exceptions.PropertyCommunicationError;
import com.vanguardtech.storepulse.platform.shared.domain.exceptions.BusinessRuleException;
import jakarta.persistence.Embeddable;
import java.util.List;

/**
 * Supporting document of a message, such as a bill, a receipt or a photo: PDF or image of up to
 * 2 MB. It never changes once created.
 */
@Embeddable
public record Attachment(String fileName, String url, String mimeType, Long sizeBytes) {

    public static final long MAX_SIZE_BYTES = 2L * 1024 * 1024;
    public static final List<String> ALLOWED_MIME_TYPES = List.of("application/pdf", "image/jpeg", "image/png", "image/webp");

    public Attachment {
        if (fileName == null || fileName.isBlank() || url == null || url.isBlank())
            throw new BusinessRuleException(PropertyCommunicationError.INVALID_ATTACHMENT);
        if (mimeType == null || !ALLOWED_MIME_TYPES.contains(mimeType))
            throw new BusinessRuleException(PropertyCommunicationError.INVALID_ATTACHMENT);
        if (sizeBytes == null || sizeBytes < 0)
            throw new BusinessRuleException(PropertyCommunicationError.INVALID_ATTACHMENT);
        if (sizeBytes > MAX_SIZE_BYTES)
            throw new BusinessRuleException(PropertyCommunicationError.ATTACHMENT_TOO_LARGE);
        fileName = fileName.trim();
    }
}
