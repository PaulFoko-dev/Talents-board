package com.talentsboard.backend.service;

import com.google.cloud.Timestamp;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Audit des accès CV : enregistre un document dans Firestore collection "cv_access_log".
 *
 * Exemple document :
 * {
 *   userId: "candidateUid",
 *   accessorUid: "companyOrAdminUid",
 *   accessorType: "ENTREPRISE"|"ADMIN"|"CANDIDAT",
 *   action: "DOWNLOAD"|"VIEW"|"DELETE"|"UPLOAD",
 *   timestamp: Timestamp.now(),
 *   ip: "1.2.3.4" (optionnel)
 * }
 *
 * Implémentation: ici on laisse TODO pour l'appel Firestore; tu dois injecter Firestore
 * via Firebase Admin SDK et écrire le document.
 */
@Slf4j
@Service
public class CvAuditService {

    private static final String COLLECTION = "cv_access_log";

    private Firestore db() {
        return FirestoreClient.getFirestore();
    }

    public void logAccess(String candidateUid, String accessorUid, String accessorType, String action) {
        try {
            Map<String, Object> doc = new HashMap<>();
            doc.put("userId", candidateUid);
            doc.put("accessorUid", accessorUid);
            doc.put("accessorType", accessorType);
            doc.put("action", action);
            doc.put("timestamp", Timestamp.now());

            DocumentReference docRef = db().collection(COLLECTION).add(doc).get();

            log.info("Audit CV logged with ID: {} - Data: {}", docRef.getId(), doc);
        } catch (InterruptedException | ExecutionException e) {
            log.error("Impossible d'enregistrer le log d'accès CV dans Firestore (Future/Execution Error)", e);
        } catch (Exception e) {
            log.error("Impossible d'enregistrer le log d'accès CV (General Error)", e);
        }
    }
}
