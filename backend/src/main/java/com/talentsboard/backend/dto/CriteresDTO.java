package com.talentsboard.backend.dto;

import lombok.Data;

import java.util.List;

/**
 * DTO exposant les critères extraits/validés côté client.
 */
@Data
public class CriteresDTO {
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

    // Ajout de languages ici car c'est un critère de matching
    private List<String> languages;
}