package com.talentsboard.backend.model;

import lombok.Data;

/**
 * Notification
 * Représente un document stocké dans Firestore.
 */
@Data
public class Notification {
    private String id;
    private String destinataireId;
    private String titre;
    private String message;
    private String type;          // nouveau_ticket, candidature, statut, systeme
    private String statut;        // lu, non_lu
    private String dateCreation;
}
