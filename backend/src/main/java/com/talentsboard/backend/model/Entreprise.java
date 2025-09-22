package com.talentsboard.backend.model;

import lombok.*;

import java.util.List;

/**
 * Représente une entreprise (hérite de User)
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class Entreprise extends User {
    private String secteur;             // Secteur d’activité (ex: IT, Finance, etc.)
    private String localisation;        // Adresse ou ville
    private String siteWeb;             // Lien vers le site web

    public Entreprise() {
        this.type = UserType.ENTREPRISE;
    }
}
