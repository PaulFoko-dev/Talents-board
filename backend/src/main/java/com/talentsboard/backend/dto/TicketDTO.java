package com.talentsboard.backend.dto;

import com.talentsboard.backend.model.Ticket.TicketType;
import com.talentsboard.backend.model.Ticket.TicketStatus;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
public class TicketDTO {
    private Long id;
    private Long ownerId;
    private String ownerName;

    @NotNull
    private TicketType type;

    @NotBlank
    private String title;

    private String description;
    private String skills;
    private String location;
    private TicketStatus status;
    private String createdAt;
}
