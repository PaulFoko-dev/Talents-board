package com.talentsboard.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Classe générique pour formater toutes les réponses d’API.
 * @param <T> le type de données renvoyé (User, AuthResponse, etc.)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    private int status;       // Code HTTP (200, 400, 409, 500...)
    private String message;   // Description lisible ("Succès", "Email déjà utilisé", etc.)
    private T data;           // Données utiles (User, AuthResponse, null si erreur)
}
