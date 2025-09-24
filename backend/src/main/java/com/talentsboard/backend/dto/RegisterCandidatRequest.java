package com.talentsboard.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO pour l’inscription d’un candidat.
 */
@Data
public class RegisterCandidatRequest {
    @NotBlank(message = "Nom requis")
    private String nom;

    @NotBlank(message = "Prénom requis")
    private String prenom;

    @Email(message = "Email invalide")
    @NotBlank(message = "Email requis")
    private String email;

    @NotBlank(message = "Mot de passe requis")
    private String motDePasse;
}
