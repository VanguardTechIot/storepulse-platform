package com.vanguardtech.storepulse.platform.propertycommunication.interfaces.rest.resources;

public record AttachmentResource(String fileName, String url, String mimeType, Long sizeBytes) {
}
