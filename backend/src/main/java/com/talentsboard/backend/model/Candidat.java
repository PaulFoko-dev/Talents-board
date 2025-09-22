package com.talentsboard.backend.model;

import lombok.*;

import java.util.List;

/**
 * Représente un candidat (utilisateur de type CANDIDAT).
 * Contient les champs spécifiques au candidat.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class Candidat extends User {
    private String prenom;              // Prénom du candidat
    private List<String> competences;   // Liste des compétences
    private String cvUrl;               // Lien du CV PDF stocké dans Firebase Storage

    public Candidat() {
        this.type = UserType.CANDIDAT;
    }
}
