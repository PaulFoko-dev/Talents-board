package com.talentsboard.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO pour l’inscription d’une entreprise.
 */
@Data
public class RegisterEntrepriseRequest {
    @NotBlank(message = "Raison sociale requise")
    private String nom;

    @Email(message = "Email invalide")
    @NotBlank(message = "Email requis")
    private String email;

    @NotBlank(message = "Mot de passe requis")
    private String motDePasse;

    @NotBlank(message = "Secteur requis")
    private String secteur;

    @NotBlank(message = "Localisation requise")
    private String localisation;

    private String siteWeb; // Optionnel
}
