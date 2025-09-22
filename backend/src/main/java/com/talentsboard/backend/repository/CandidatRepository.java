package com.talentsboard.backend.repository;

import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import com.talentsboard.backend.model.Candidat;
import org.springframework.stereotype.Repository;

import java.util.concurrent.ExecutionException;

@Repository
public class CandidatRepository {
    private final Firestore db = FirestoreClient.getFirestore();

    public void save(Candidat candidat) {
        db.collection("candidats").document(candidat.getId()).set(candidat);
    }

    public Candidat findById(String id) throws ExecutionException, InterruptedException {
        return db.collection("candidats").document(id).get().get().toObject(Candidat.class);
    }
}
