package com.talentsboard.backend.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Représente un candidat (hérite de User).
 * Ajout des champs spécifiques : prénom.
 * Les informations avancées (compétences, CV) seront
 * gérées via une entité ProfilCandidat.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class Candidat extends User {
    private String prenom; // Prénom du candidat

    public Candidat() {
        this.type = UserType.CANDIDAT;
    }
}
