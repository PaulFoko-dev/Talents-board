package com.talentsboard.backend.mapper;

import com.talentsboard.backend.dto.UserDTO;
import com.talentsboard.backend.model.User;

/**
 * Mapper simple User -> UserDTO.
 * Centralise la logique de transformation.
 */
public final class UserMapper {

    private UserMapper() {}

    public static UserDTO toDTO(User user) {
        if (user == null) return null;
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setNom(user.getNom());
        dto.setType(user.getType() != null ? user.getType().name() : null);
        dto.setDescription(user.getDescription());
        if (user.getDateInscription() != null) {
            dto.setDateInscription(user.getDateInscription().toDate().toInstant().toString());
        }
        dto.setPrenom(user.getPrenom());
        dto.setCompetences(user.getCompetences());
        dto.setCv(user.getCv());
        dto.setPhotoProfil(user.getPhotoProfil());
        dto.setLogo(user.getLogo());
        dto.setSecteur(user.getSecteur());
        dto.setLocalisation(user.getLocalisation());
        dto.setSiteWeb(user.getSiteWeb());
        dto.setCvPath(user.getCvPath());
        return dto;
    }

    /**
     * Applique les champs modifiables d'un DTO à l'entité.
     * - n'applique pas l'id ni le type (bloqués pour la sécurité).
     */
    public static void applyUpdateToEntity(UserDTO request, User entity) {
        if (request.getNom() != null) entity.setNom(request.getNom());
        if (request.getEmail() != null) entity.setEmail(request.getEmail());
        if (request.getDescription() != null) entity.setDescription(request.getDescription());
        if (request.getPrenom() != null) entity.setPrenom(request.getPrenom());
        if (request.getCompetences() != null) entity.setCompetences(request.getCompetences());
        if (request.getCv() != null) entity.setCv(request.getCv());
        if (request.getPhotoProfil() != null) entity.setPhotoProfil(request.getPhotoProfil());
        if (request.getLogo() != null) entity.setLogo(request.getLogo());
        if (request.getSecteur() != null) entity.setSecteur(request.getSecteur());
        if (request.getLocalisation() != null) entity.setLocalisation(request.getLocalisation());
        if (request.getSiteWeb() != null) entity.setSiteWeb(request.getSiteWeb());
    }
}
