package com.talentsboard.backend.model;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Critères associés à un ticket.
 * Remarque: entreprise et candidat peuvent remplir des champs différents.
 */
@Data
@NoArgsConstructor
public class CritereTicket {
    private String localisation;
    private String typeContrat;        // CDI, CDD, Stage, Freelance
    private Double salaireMin;
    private Double salaireMax;
    private String domaine;            // IT, Finance, etc.
}