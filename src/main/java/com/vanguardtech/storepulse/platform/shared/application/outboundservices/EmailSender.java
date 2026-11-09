package com.vanguardtech.storepulse.platform.shared.application.outboundservices;

/** Delivers plain-text emails for any bounded context. */
public interface EmailSender {

    /**
     * @param to      recipient address
     * @param subject subject line
     * @param text    plain-text body
     * @throws IllegalStateException when the email could not be delivered
     */
    void send(String to, String subject, String text);
}