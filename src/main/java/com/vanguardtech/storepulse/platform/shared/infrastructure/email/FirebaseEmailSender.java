package com.vanguardtech.storepulse.platform.shared.infrastructure.email;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import com.vanguardtech.storepulse.platform.shared.application.outboundservices.EmailSender;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Delivers emails through the Firebase "Trigger Email from Firestore" extension: it writes a
 * document with the recipient and the message to the collection the extension listens to, and the
 * extension sends the email. Active only when the service account key file is configured.
 */
@Service
@ConditionalOnExpression("!'${storepulse.firebase.credentials-path:}'.isBlank()")
public class FirebaseEmailSender implements EmailSender {

    private static final String FIREBASE_APP_NAME = "storepulse";

    private final String credentialsPath;
    private final String mailCollection;
    private Firestore firestore;

    public FirebaseEmailSender(@Value("${storepulse.firebase.credentials-path}") String credentialsPath,
                               @Value("${storepulse.firebase.mail-collection}") String mailCollection) {
        this.credentialsPath = credentialsPath;
        this.mailCollection = mailCollection;
    }

    @Override
    public void send(String to, String subject, String text) {
        var mail = Map.of("to", to, "message", Map.of("subject", subject, "text", text));
        try {
            firestore().collection(mailCollection).add(mail).get();
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("The email could not be delivered", exception);
        } catch (ExecutionException | IOException exception) {
            throw new IllegalStateException("The email could not be delivered", exception);
        }
    }

    /** Opens the Firestore connection the first time it is needed and reuses it afterwards. */
    private synchronized Firestore firestore() throws IOException {
        if (firestore != null) return firestore;
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