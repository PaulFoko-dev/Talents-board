package com.talentsboard.backend.model;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Classe abstraite représentant un utilisateur générique
 * (héritée par Candidat et Entreprise).
 */
@Data
@NoArgsConstructor
public abstract class User {
    protected String id = UUID.randomUUID().toString();;                    // UID Firebase (sera écrasé à l'inscription)
    protected String email;                                                 // Email unique
    protected String nom;                                                   // Nom complet ou raison sociale
    protected UserType type;                                                // Enum : CANDIDAT, ENTREPRISE, ADMIN
    protected String description;                                           // Bio ou présentation
    protected LocalDateTime dateInscription = LocalDateTime.now();;         // Date d’inscription
}
