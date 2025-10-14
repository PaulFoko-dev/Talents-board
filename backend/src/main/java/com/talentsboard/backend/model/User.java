package com.talentsboard.backend.model;

import com.google.cloud.Timestamp;
import lombok.Data;

/**
 * Entité utilisateur centralisée (persistée dans Firestore).
 * - Un seul modèle pour Candidat/Entreprise/ADMIN via le champ `type`.
 * - Champs spécifiques laissés optionnels (peuvent être null).
 */
@Data
public class User {
    private String id;               // UID Firebase (ou UUID si non encore créé)
    private String email;
    private String nom;              // pour candidat = nom, pour entreprise = raison sociale
    private UserType type;
    private String description;

    // Champs spécifiques candidat
    private String prenom;
    private String competences;
    private String cv;           // URL du CV
    private String photoProfil;
    private String numero;

    // Champs spécifiques entreprise
    private String logo;
    private String secteur;
    private String localisation;
    private String siteWeb;

    private Timestamp dateInscription = Timestamp.now();
    private String cvPath;
}