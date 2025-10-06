package com.talentsboard.backend.dto;

import lombok.Data;

import java.util.List;

/**
 * DTO pour valider un ticket entreprise (après modifications user).
 */
@Data
public class ValidateTicketEntrepriseRequest {
    private String title;
    private String descriptionRaw;

    // --- Champs Géographiques & Contrat ---
    private String localisation;
    private String domaine; // Utilisé souvent comme nom de l'entreprise ou secteur
    private String typeContrat;

    // --- Salaire ---
    private Double salaireMin;
    private Double salaireMax;

    // --- Expérience & Diplôme ---
    private String niveauExperience;
    private String diplomeRequis; // Ajouté pour la complétude

    // --- Télétravail (Validé par l'utilisateur) ---
    private String modeTravail;              // FULL_REMOTE | PARTIAL_REMOTE | HYBRID | ONSITE | OPTIONAL_REMOTE
    private Integer teletravailJourParSemaine;
    private Double teletravailPourcentage;

    // --- Compétences & Avantages (Listes de chaînes normalisées) ---
    private List<String> avantages;
    private List<String> competences;

    private List<String> languages;
}
