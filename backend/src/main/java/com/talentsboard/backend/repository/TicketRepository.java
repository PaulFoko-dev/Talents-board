package com.talentsboard.backend.repository;

import com.talentsboard.backend.model.Ticket;

import java.util.List;
import java.util.concurrent.ExecutionException;
/**
 * TicketRepository
 * Interface pour accéder aux données ticket dans Firestore.
 */
public interface TicketRepository {
    Ticket save(Ticket ticket) throws ExecutionException, InterruptedException;
    Ticket findById(String id) throws ExecutionException, InterruptedException;
    List<Ticket> findAllPublished(int limit, String pageToken) throws ExecutionException, InterruptedException;
    List<Ticket> findByOwner(String ownerUid, int limit, String pageToken) throws ExecutionException, InterruptedException;
    void deleteById(String id) throws ExecutionException, InterruptedException;
    List<Ticket> findBySkill(String skill, int limit) throws ExecutionException, InterruptedException;
}