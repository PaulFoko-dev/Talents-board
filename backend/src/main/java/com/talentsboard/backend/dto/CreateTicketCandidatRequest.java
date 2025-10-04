package com.talentsboard.backend.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;

/**
 * Requête pour créer un ticket candidat.
 * - Le front doit d'abord uploader le fichier et récupérer cvPath via /api/files/cv.
 * - Puis appeler ce endpoint en fournissant cvPath.
 */
@Data
public class CreateTicketCandidatRequest {
    @NotBlank
    private String title;
    private String descriptionRaw; // optional, can be empty if extraction from CV
    private String localisation;
    private String domaine;
    private String availability; // Ex: "Immédiate", "Sous 1 mois", "Sous 3 mois"

    // --- Attentes Matching (pour Criteres) ---
    private Double salaireMinExpectation; // Attente salariale min
    private Double salaireMaxExpectation; // Attente salariale max

    private String niveauExperience;      // Ex: Junior, Confirmé, Senior

    // ✅ Champ de préférence de localisation pour le scoring
    private Boolean preferExactLocation;
}