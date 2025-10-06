package com.talentsboard.backend.dto;
import com.talentsboard.backend.model.UserType;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * DTO renvoyé au client pour visualiser un ticket.
 */
// TicketDTO.java
@Data
public class TicketDTO {
    private String id;
    private String ownerUid;
    private UserType ownerType;
    private String status;
    private String title;
    private String descriptionRaw;
    private String company;
    private String domaine;
    private String salaryRange;
    private String availability; // Info Candidat

    // --- Critères normalisés pour l'affichage/filtres ---
    private String localisation;
    private String typeContrat;
    private String niveauExperience;
    private List<String> competences;
    private List<String> languages;
    private List<String> avantages;

    // Champs télétravail
    private String modeTravail; // Normalisé (FULL_REMOTE, etc.)
    private Integer teletravailJourParSemaine;

    // Score pour le filtrage rapide (non affiché directement)
    private Map<String,Object> scoreDenorm;
}