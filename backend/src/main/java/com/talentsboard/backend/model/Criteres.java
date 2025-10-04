package com.talentsboard.backend.model;
import lombok.Data;

import java.util.List;

/**
 * Critères associés à un ticket (recherche emploi ou offre entreprise)
 */
@Data
public class Criteres {
    private String localisation;
    private List<String> competences;
    private String domaine;
    private String typeContrat;
    private Double salaireMin;
    private Double salaireMax;
    private String niveauExperience;

    // Télétravail
    private String modeTravail;
    private Integer teletravailJourParSemaine;
    private Double teletravailPourcentage;

    private String diplomeRequis;
    private List<String> avantages;

    // Ajout de languages ici
    private List<String> languages;
    private Boolean preferExactLocation;
}