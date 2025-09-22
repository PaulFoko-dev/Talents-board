package com.talentsboard.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO pour la requête de connexion.
 *
 * NOTE: Le flux recommandé est que le frontend utilise le SDK Firebase pour se connecter
 * (signInWithEmailAndPassword) puis envoie l'ID token au backend. Ici nous acceptons :
 *  - idToken (recommandé)  OR
 *  - email + motDePasse (non recommandé côté serveur, voir documentation)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthRequest {
    @Email(message = "Email invalide")
    @NotBlank(message = "Email requis")
    private String email;

    @NotBlank(message = "Mot de passe requis")
    private String motDePasse;
}