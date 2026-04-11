package com.talentsboard.backend.dto;

import com.talentsboard.backend.model.User.UserRole;
import lombok.Data;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
public class UserDTO {
    private Long id;

    @NotBlank
    private String firebaseUid;

    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String displayName;

    @NotNull
    private UserRole role;

    private String company;
    private String bio;
    private String cvUrl;
    private String skills;
    private String createdAt;
}
