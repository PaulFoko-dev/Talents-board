package com.talentsboard.backend.repository.impl;

import com.google.api.core.ApiFuture;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import com.talentsboard.backend.model.Ticket;
import com.talentsboard.backend.repository.TicketRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * Impl via Firestore. Simple queries, pas d'indexation avancée.
 */
@Repository
public class TicketRepositoryImpl implements TicketRepository {

    private static final String COLLECTION = "tickets";

    private Firestore db() {
        return FirestoreClient.getFirestore();
    }

    @Override
    public Ticket save(Ticket ticket) throws ExecutionException, InterruptedException {
        if (ticket.getId() == null) {
            DocumentReference docRef = db().collection(COLLECTION).document();
            ticket.setId(docRef.getId());
            ticket.setCreatedAt(Timestamp.now());
        }
        ticket.setUpdatedAt(Timestamp.now());
        ApiFuture<WriteResult> f = db().collection(COLLECTION).document(ticket.getId()).set(ticket);
        f.get();
        return ticket;
    }

    @Override
    public Ticket findById(String id) throws ExecutionException, InterruptedException {
        DocumentSnapshot snap = db().collection(COLLECTION).document(id).get().get();
        return snap.exists() ? snap.toObject(Ticket.class) : null;
    }

    @Override
    public List<Ticket> findAllPublished(int limit, String pageToken) throws ExecutionException, InterruptedException {
        Query q = db().collection(COLLECTION)
                .whereEqualTo("status", "PUBLISHED")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .limit(limit);
        // pageToken handling omitted for brevity — implement cursor with startAfter if passed
        List<QueryDocumentSnapshot> docs = q.get().get().getDocuments();
        return docs.stream().map(d -> d.toObject(Ticket.class)).collect(Collectors.toList());
    }

    @Override
    public List<Ticket> findByOwner(String ownerUid, int limit, String pageToken) throws ExecutionException, InterruptedException {
        Query q = db().collection(COLLECTION)
                .whereEqualTo("ownerUid", ownerUid)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .limit(limit);
        List<QueryDocumentSnapshot> docs = q.get().get().getDocuments();
        return docs.stream().map(d -> d.toObject(Ticket.class)).collect(Collectors.toList());
    }

    @Override
    public void deleteById(String id) throws ExecutionException, InterruptedException {
        db().collection(COLLECTION).document(id).delete().get();
    }

    @Override
    public List<Ticket> findBySkill(String skill, int limit) throws ExecutionException, InterruptedException {
        // simple approach: query where criteres.competences array contains skill
        Query q = db().collection(COLLECTION)
                .whereEqualTo("status", "PUBLISHED")
                .whereArrayContains("criteres.competences", skill)
                .limit(limit);
        List<QueryDocumentSnapshot> docs = q.get().get().getDocuments();
        return docs.stream().map(d -> d.toObject(Ticket.class)).collect(Collectors.toList());
    }
}