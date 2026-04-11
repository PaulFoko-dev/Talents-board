package com.talentsboard.backend.dto;

import com.talentsboard.backend.model.Application.ApplicationStatus;
import lombok.Data;
import jakarta.validation.constraints.NotNull;

@Data
public class ApplicationDTO {
    private Long id;

    @NotNull
    private Long candidateId;

    @NotNull
    private Long ticketId;

    private String message;
    private String cvUrl;
    private ApplicationStatus status;
    private String createdAt;
}
