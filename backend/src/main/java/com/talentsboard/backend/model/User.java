package com.talentsboard.backend.model;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Classe abstraite représentant un utilisateur générique.
 * - Héritée par Candidat et Entreprise.
 * - Contient les informations communes.
 */
@Data
public abstract class User {
    protected String id = UUID.randomUUID().toString();     // UID Firebase (sera remplacé après inscription)
    protected String email;                                 // Email unique
    protected String nom;                                   // Nom (Candidat) ou Raison sociale (Entreprise)
    protected UserType type;                                // Enum : CANDIDAT, ENTREPRISE, ADMIN
    protected String description;                           // Présentation ou bio          
    protected LocalDateTime dateInscription = LocalDateTime.now(); // Date d’inscription
}