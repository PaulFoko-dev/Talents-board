package com.talentsboard.backend.mapper;

import com.talentsboard.backend.dto.TicketDTO;
import com.talentsboard.backend.model.Criteres;
import com.talentsboard.backend.model.Ticket;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TicketMapper {

    public static TicketDTO toDTO(Ticket ticket) {
        if (ticket == null) return null;

        Criteres c = ticket.getCriteres();
        TicketDTO dto = new TicketDTO();

        dto.setId(ticket.getId());
        dto.setOwnerUid(ticket.getOwnerUid());
        dto.setOwnerType(ticket.getOwnerType());
        dto.setStatus(ticket.getStatus());
        dto.setTitle(ticket.getTitle());
        dto.setDescriptionRaw(ticket.getRawText());
        dto.setCompany(ticket.getCompany());
        dto.setDomaine(ticket.getDomaine());
        dto.setSalaryRange(ticket.getSalaryRange());
        dto.setAvailability(ticket.getAvailability());

        // --- Mapping des Criteres vers le DTO ---
        if (c != null) {
            dto.setLocalisation(c.getLocalisation());
            dto.setTypeContrat(c.getTypeContrat());
            dto.setNiveauExperience(c.getNiveauExperience());
            dto.setCompetences(c.getCompetences());
            dto.setLanguages(c.getLanguages());
            dto.setAvantages(c.getAvantages());

            // Télétravail
            dto.setModeTravail(c.getModeTravail());
            dto.setTeletravailJourParSemaine(c.getTeletravailJourParSemaine());
        }

        // Score (pour debug/infos)
        dto.setScoreDenorm(ticket.getScoreDenorm());

        return dto;
    }

    // Si vous avez aussi une méthode toEntity() pour les mises à jour, elle doit être revue.
}