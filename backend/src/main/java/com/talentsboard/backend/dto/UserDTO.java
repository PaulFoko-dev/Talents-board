package com.talentsboard.backend.dto;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * UserDTO
 * Objet de transfert pour user.
 * Sert à communiquer entre frontend et backend.
 */
@Data
public class UserDTO {
    // TODO: Définir les champs exposés dans l'API
    private String id;                  // UID Firebase ou UUID généré
    private String email;               // Email de connexion
    private String nom;                 // Nom complet ou Raison sociale
    private String type;                // CANDIDAT, ENTREPRISE, ADMIN (enum sous forme de string)
    private String description;         // Bio / présentation
    private LocalDateTime dateInscription; // Date d’inscription
}
