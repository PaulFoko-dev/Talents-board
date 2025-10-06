package com.talentsboard.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * DTO de réponse après une connexion réussie.
 */
@Data
@AllArgsConstructor
public class AuthResponse {
    private String token;   // JWT Firebase ou custom
    private String userId;  // UID utilisateur
    private String typeUser;
}
