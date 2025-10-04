package com.talentsboard.backend.dto;
import lombok.Data;
import java.time.LocalDateTime;

import com.google.cloud.Timestamp;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private String competences;
    private String cv;           // URL du CV
    private String photoProfil;

    // entreprise
    private String logo;
    private String secteur;
    private String localisation;
    private String siteWeb;

    private String dateInscription;

}
