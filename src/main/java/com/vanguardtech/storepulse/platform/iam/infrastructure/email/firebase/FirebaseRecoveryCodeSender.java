package com.vanguardtech.storepulse.platform.iam.infrastructure.email.firebase;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import com.vanguardtech.storepulse.platform.iam.application.internal.outboundservices.RecoveryCodeSender;
import com.vanguardtech.storepulse.platform.iam.domain.model.aggregates.PasswordRecovery;
import com.vanguardtech.storepulse.platform.iam.domain.model.valueobjects.Email;
import com.vanguardtech.storepulse.platform.iam.domain.model.valueobjects.PasswordResetCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * {@link RecoveryCodeSender} that delivers the code through the Firebase "Trigger Email from
 * Firestore" extension: it writes a document with the recipient and the message to the collection
 * the extension listens to, and the extension sends the email.
 *
 * <p>The connection is opened on the first delivery, so the application starts even where Firebase
 * is not configured. It needs {@code storepulse.iam.firebase.credentials-path} (service account key
 * file) and {@code storepulse.iam.firebase.mail-collection}.
 */
@Slf4j
@Service
public class FirebaseRecoveryCodeSender implements RecoveryCodeSender {

    private static final String FIREBASE_APP_NAME = "storepulse-iam";
    private static final String SUBJECT = "Código de recuperación de StorePulse";
    private static final String TEXT = """
            Tu código para restablecer la contraseña de StorePulse es %s.

            El código vence en %d minutos y solo puede usarse una vez.
            Si no solicitaste este cambio, ignora este mensaje: tu contraseña no cambiará.""";

    private final String credentialsPath;
    private final String mailCollection;
    private Firestore firestore;

    /**
     * @param credentialsPath path of the Firebase service account key file
     * @param mailCollection  Firestore collection the email extension listens to
     */
    public FirebaseRecoveryCodeSender(@Value("${storepulse.iam.firebase.credentials-path}") String credentialsPath,
                                      @Value("${storepulse.iam.firebase.mail-collection}") String mailCollection) {
        this.credentialsPath = credentialsPath;
        this.mailCollection = mailCollection;
    }

    @Override
    public void send(Email email, PasswordResetCode code, Instant expiresAt) {
        Map<String, Object> mail = Map.of(
                "to", email.address(),
                "message", Map.of(
                        "subject", SUBJECT,
                        "text", TEXT.formatted(code.value(), PasswordRecovery.VALIDITY.toMinutes())));
        try {
            firestore().collection(mailCollection).add(mail).get();
            log.info("Password recovery code queued for delivery, valid until {}", expiresAt);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("The password recovery code could not be delivered", exception);
        } catch (ExecutionException | IOException exception) {
            throw new IllegalStateException("The password recovery code could not be delivered", exception);
        }
    }

    /** Opens the Firestore connection the first time it is needed and reuses it afterwards. */
    private synchronized Firestore firestore() throws IOException {
        if (firestore != null) return firestore;
        if (credentialsPath == null || credentialsPath.isBlank())
            throw new IllegalStateException("storepulse.iam.firebase.credentials-path is not configured");

        var firebaseApp = FirebaseApp.getApps().stream()
                .filter(app -> app.getName().equals(FIREBASE_APP_NAME))
                .findFirst()
                .orElse(null);
        if (firebaseApp == null) {
            try (var credentials = new FileInputStream(credentialsPath)) {
                var options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(credentials))
                        .build();
                firebaseApp = FirebaseApp.initializeApp(options, FIREBASE_APP_NAME);
            }
        }
        firestore = FirestoreClient.getFirestore(firebaseApp);
        return firestore;
    }
}
