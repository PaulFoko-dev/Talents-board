package com.talentsboard.backend.controller;

import com.talentsboard.backend.dto.ApiResponse;
import com.talentsboard.backend.dto.UserDTO;
import com.talentsboard.backend.mapper.UserMapper;
import com.talentsboard.backend.model.User;
import com.talentsboard.backend.service.AuthService;
import com.talentsboard.backend.service.UserService;

import java.util.concurrent.ExecutionException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Endpoints liés au profil utilisateur connecté.
 */
@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    private final UserService userService;
    private final AuthService authService;

    public ProfileController(UserService userService, AuthService authService) {
        this.userService = userService;
        this.authService = authService;
    }

    /**
     * 🔹 1. Récupérer le profil de l’utilisateur connecté
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserDTO>> getUser(@PathVariable String id) throws ExecutionException, InterruptedException {
        User u = userService.getUserById(id);
        if (u == null) return ResponseEntity.status(404).body(new ApiResponse<>(404, "Utilisateur non trouvé", null));
        return ResponseEntity.ok(new ApiResponse<>(200, "Succès", UserMapper.toDTO(u)));
    }

    /**
     * 🔹 2. Modifier le profil de l’utilisateur connecté
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserDTO>> updateUser(@PathVariable String id, @RequestBody UserDTO dto) throws Exception {
        User existing = userService.getUserById(id);
        if (existing == null) return ResponseEntity.status(404).body(new ApiResponse<>(404, "Utilisateur non trouvé", null));

        // for security: ensure incoming dto.id/type are ignored
        dto.setId(existing.getId());
        dto.setType(existing.getType() != null ? existing.getType().name() : null);

        // apply updates
        com.talentsboard.backend.mapper.UserMapper.applyUpdateToEntity(dto, existing);

        User updated = authService.updateUserProfile(id, existing);
        return ResponseEntity.ok(new ApiResponse<>(200, "Profil mis à jour", UserMapper.toDTO(updated)));
    }

}
