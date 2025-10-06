package com.talentsboard.backend.mapper;

import com.talentsboard.backend.dto.CriteresDTO;
import com.talentsboard.backend.model.Criteres;

public class CriteresMapper {
    public static CriteresDTO toDTO(Criteres c) {
        if (c == null) return null;

        CriteresDTO dto = new CriteresDTO();
        dto.setLocalisation(c.getLocalisation());
        dto.setCompetences(c.getCompetences());
        dto.setDomaine(c.getDomaine());
        dto.setTypeContrat(c.getTypeContrat());
        dto.setSalaireMin(c.getSalaireMin());
        dto.setSalaireMax(c.getSalaireMax());
        dto.setNiveauExperience(c.getNiveauExperience());
        dto.setModeTravail(c.getModeTravail());
        dto.setTeletravailJourParSemaine(c.getTeletravailJourParSemaine());
        dto.setTeletravailPourcentage(c.getTeletravailPourcentage());
        dto.setDiplomeRequis(c.getDiplomeRequis());
        dto.setAvantages(c.getAvantages());
        dto.setLanguages(c.getLanguages()); // Mappage du nouveau champ

        return dto;
    }
}
