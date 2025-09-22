package com.talentsboard.backend.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Représente une entreprise (hérite de User).
 * Les informations de base sont stockées ici.
 * Les informations plus avancées seront gérées via ProfilEntreprise.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class Entreprise extends User {
    private String secteur;      // Secteur d’activité (ex: IT, Finance…)
    private String localisation; // Ville ou adresse
    private String siteWeb;      // Site web officiel

    public Entreprise() {
        this.type = UserType.ENTREPRISE;
    }
}
