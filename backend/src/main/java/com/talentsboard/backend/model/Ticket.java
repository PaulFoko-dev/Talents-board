package com.talentsboard.backend.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.google.cloud.Timestamp;
import lombok.Data;

/**
 * Ticket créé par un candidat ou une entreprise
 */
@Data
public class Ticket {
    private String id;
    private String ownerUid;
    private UserType ownerType;
    private String status;

    // --- Infos brutes & Affichage ---
    private String title;
    private String rawText;

    // Champs de matching supprimés : location, contractType, competences, experienceLevel, benefits, languages.

    private String salaryRange;
    private Map<String,Object> scoreDenorm;

    // --- Infos entreprise ---
    private String company;

    // ✅ Le point d'entrée pour les critères structurés
    private Criteres criteres;

    // --- Infos candidat ---
    private String availability;
    private String domaine;

    // --- Timestamps ---
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private Timestamp validatedAt;

    // --- Télétravail enrichi ---
    private String modeTravailRaw;
    private Double teletravailEquivalentDaysPerWeek;
}