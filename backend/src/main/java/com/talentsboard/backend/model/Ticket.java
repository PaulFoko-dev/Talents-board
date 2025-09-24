package com.talentsboard.backend.model;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Ticket créé par un candidat ou une entreprise
 */
public class Ticket {
    private String id = UUID.randomUUID().toString();
    private String userId;              // ID du créateur (candidat ou entreprise)
    private CritereTicket criteres;
    private String description;
    private LocalDateTime dateCreation = LocalDateTime.now();

    // Getters / Setters
}
