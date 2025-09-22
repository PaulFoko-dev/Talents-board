package com.talentsboard.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO générique pour exposer un utilisateur sans données sensibles.
 * Lombok est utilisé pour générer automatiquement les getters/setters
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private String id;
    private String type;
    private String nom;
    private String email;
    private String description;
    private LocalDateTime dateInscription;
}