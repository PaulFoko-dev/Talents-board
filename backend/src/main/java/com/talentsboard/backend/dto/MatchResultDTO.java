package com.talentsboard.backend.dto;

import lombok.Data;

import java.util.List;

/**
 * Résultat de matching pour un ticket.
 */
@Data
public class MatchResultDTO {
    private String ticketId;
    private String ownerUid;
    private Double score;
    private List<String> matchedSkills;
}
