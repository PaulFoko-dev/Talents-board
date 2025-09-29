package com.talentsboard.backend.model;
import lombok.Data;

/**
 * Critères associés à un ticket (recherche emploi ou offre entreprise)
 */
@Data
public class CritereTicket {
    private String localisation;
    private String niveau; 
    private String typeContrat;   // CDI, CDD, Stage, Freelance
    private Double salaireMin;
    private Double salaireMax;
    private String domaine;

    // Getters / Setters
}
