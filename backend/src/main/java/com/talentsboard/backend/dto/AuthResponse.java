package com.talentsboard.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de réponse d'authentification.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    /** Le token Firebase (JWT / custom token) */
    private String token;

    /** UID Firebase de l'utilisateur */
    private String userId;
}