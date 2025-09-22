package com.talentsboard.backend.model;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Ticket minimal : représente une demande/opportunité.
 */
@Data
@NoArgsConstructor
public class Ticket {
    private String id = UUID.randomUUID().toString();
    private String titre;
    private String description;
    private List<String> competences;
    private CritereTicket criteres;
    private String createurId;      // id du User qui créé le ticket
    private UserType createurType;  // CANDIDAT ou ENTREPRISE
    private LocalDateTime dateCreation = LocalDateTime.now();
}

