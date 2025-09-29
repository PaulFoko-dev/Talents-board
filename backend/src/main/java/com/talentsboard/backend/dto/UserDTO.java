package com.talentsboard.backend.dto;

import com.google.cloud.Timestamp;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO exposé par l'API pour tous les endpoints utilisateurs.
 * Permet d'éviter les problèmes de polymorphisme et d'exposer
 * uniquement ce que l'on souhaite.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private String id;
    private String email;
    private String nom;
    private String type; // "CANDIDAT" | "ENTREPRISE" | "ADMIN"
    private String description;

    // candidat
    private String prenom;

    // entreprise
    private String secteur;
    private String localisation;
    private String siteWeb;

    private String dateInscription;

}
