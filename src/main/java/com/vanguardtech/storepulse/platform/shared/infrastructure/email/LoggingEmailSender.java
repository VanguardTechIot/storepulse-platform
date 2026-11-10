package com.vanguardtech.storepulse.platform.shared.infrastructure.email;

import com.vanguardtech.storepulse.platform.shared.application.outboundservices.EmailSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Service;

/**
 * Local development delivery: writes each email to the log instead of sending it. Active only when
 * Firebase is not configured; the production profile requires Firebase, so it never runs there.
 */
@Slf4j
@Service
@ConditionalOnExpression("'${storepulse.firebase.credentials-path:}'.isBlank()")
public class LoggingEmailSender implements EmailSender {

    @Override
    public void send(String to, String subject, String text) {
        log.warn("Email not sent (Firebase is not configured)\nTo: {}\nSubject: {}\n{}", to, subject, text);
    }
}