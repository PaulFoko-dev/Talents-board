package com.talentsboard.backend.dto;

import lombok.Data;

@Data
public class NotificationDTO {
    private Long id;
    private Long userId;
    private String message;
    private String type;
    private boolean isRead;
    private String createdAt;
}
