package com.talentsboard.backend.dto;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * TicketDTO
 * Objet de transfert pour ticket.
 * Sert à communiquer entre frontend et backend.
 */
@Data
public class TicketDTO {
    // TODO: Définir les champs exposés dans l'API
    @NotBlank(message = "Domaine d’activité requise")
    private String domaine;        // Domaine d’activité (ex : Informatique, Marketing…)
    
    @NotBlank(message = "Ville, pays ou région requise")
    private String localisation;   // Ville, pays ou région
    
    @NotBlank(message = "Niveau recherché requise")
    private String niveau;         // Niveau recherché (ex : Junior, Senior…)
    
    @NotBlank(message = "Type de contrat requise")
    private String typeContrat;    // CDI, CDD, Alternance, Stage
    
    @NotBlank(message = "Salaire minimum requise")
    private Double salaireMin;     // Salaire minimum Optionnel
    
    @NotBlank(message = "Salaire maximum requise")
    private Double salaireMax;     // Salaire maximum Optionnel
}

    