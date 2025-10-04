package com.talentsboard.backend.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;

/**
 * Requête pour créer un ticket entreprise à partir d'un simple copier-coller d'annonce.
 * Seul le champ descriptionRaw est nécessaire ici.
 */
@Data
public class CreateTicketEntrepriseRequest {
    @NotBlank(message = "descriptionRaw est requise")
    private String descriptionRaw;
}