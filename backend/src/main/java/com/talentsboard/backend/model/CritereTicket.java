package com.talentsboard.backend.model;

/**
 * Critères associés à un ticket (recherche emploi ou offre entreprise)
 */
public class CritereTicket {
    private String localisation;
    private String typeContrat;   // CDI, CDD, Stage, Freelance
    private Double salaireMin;
    private Double salaireMax;
    private String domaine;

    // Getters / Setters
}
