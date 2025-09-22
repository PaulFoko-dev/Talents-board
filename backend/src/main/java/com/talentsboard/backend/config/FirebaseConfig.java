package com.talentsboard.backend.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;

/**
 * FirebaseConfig
 * Configuration pour initialiser Firebase Admin SDK.
 * Charge les credentials (firebase-service-account.json).
 */
@Configuration
public class FirebaseConfig {
    
    /**
     * Initialise Firebase Admin SDK au démarrage de l'application
     */
    @PostConstruct
    public void init() {
        try {
            FileInputStream serviceAccount =
                    new FileInputStream("src/main/resources/firebase-service-account.json");

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                System.out.println("👌 Firebase initialisé avec succès !");
            }
        } catch (Exception e) {
            throw new RuntimeException("😭 Erreur d'initialisation Firebase: " + e.getMessage());
        }
    }
}
