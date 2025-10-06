package com.talentsboard.backend.repository.impl;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.cloud.FirestoreClient;
import com.talentsboard.backend.model.User;
import com.talentsboard.backend.repository.UserRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * Implémentation Firestore du UserRepository.
 * Stocke les documents dans la collection "users".
 */
@Repository
public class UserRepositoryImpl implements UserRepository {

    private static final String COLLECTION = "users";

    private Firestore db() {
        return FirestoreClient.getFirestore();
    }

    @Override
    public User save(User user) throws ExecutionException, InterruptedException {
        if (user.getId() == null) {
            // si pas d'ID, utiliser l'UID Firebase (doit être défini avant save)
            user.setId(java.util.UUID.randomUUID().toString());
        }
        ApiFuture<WriteResult> f = db().collection(COLLECTION).document(user.getId()).set(user);
        f.get();
        return user;
    }

    @Override
    public User findById(String id) throws ExecutionException, InterruptedException {
        DocumentSnapshot doc = db().collection(COLLECTION).document(id).get().get();
        return doc.exists() ? doc.toObject(User.class) : null;
    }

    @Override
    public List<User> findAll() throws ExecutionException, InterruptedException {
        List<QueryDocumentSnapshot> docs = db().collection(COLLECTION).get().get().getDocuments();
        return docs.stream().map(d -> d.toObject(User.class)).collect(Collectors.toList());
    }

    @Override
    public User update(User user) throws ExecutionException, InterruptedException {
        // overwrite
        return save(user);
    }

    @Override
    public void deleteById(String id) throws ExecutionException, InterruptedException {
        // 1. Supprimer d'abord FirebaseAuth
        try {
            FirebaseAuth.getInstance().deleteUser(id);
        } catch (FirebaseAuthException e) {
            throw new RuntimeException("Échec suppression FirebaseAuth: " + e.getMessage(), e);
        }

        // 2. Puis supprimer dans Firestore
        ApiFuture<WriteResult> f = db().collection(COLLECTION).document(id).delete();
        f.get();
    }

    @Override
    public User findByEmail(String email) throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> f = db().collection(COLLECTION).whereEqualTo("email", email).get();
        List<QueryDocumentSnapshot> docs = f.get().getDocuments();
        return docs.isEmpty() ? null : docs.get(0).toObject(User.class);
    }
}
