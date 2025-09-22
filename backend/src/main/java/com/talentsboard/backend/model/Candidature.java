package com.talentsboard.backend.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Candidature spontanée d’un candidat vers une entreprise.
 */
@Data
@Getter
@Setter
public class Candidature {
    private String id = UUID.randomUUID().toString();
    private String candidatId;
    private String entrepriseId;
    private String ticketId; // Optionnel : si lié à un ticket existant
    private String message;
    private LocalDateTime dateCandidature = LocalDateTime.now();
}
