package com.talentsboard.backend.controller;

import com.talentsboard.backend.dto.ApiResponse;
import com.talentsboard.backend.dto.UserDTO;
import com.talentsboard.backend.exception.UnauthorizedException;
import com.talentsboard.backend.mapper.UserMapper;
import com.talentsboard.backend.model.User;
import com.talentsboard.backend.service.AuthService;
import com.talentsboard.backend.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * Endpoints utilisateurs ; réponse toujours UserDTO.
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final AuthService authService;

    public UserController(UserService userService, AuthService authService) {
        this.userService = userService;
        this.authService = authService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserDTO>> getUser(@PathVariable String id) throws ExecutionException, InterruptedException {
        User u = userService.getUserById(id);
        if (u == null) return ResponseEntity.status(404).body(new ApiResponse<>(404, "Utilisateur non trouvé", null));
        return ResponseEntity.ok(new ApiResponse<>(200, "Succès", UserMapper.toDTO(u)));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<UserDTO>>> getAllUsers() throws ExecutionException, InterruptedException {
        List<User> users = userService.getAllUsers();
        List<UserDTO> dtos = users.stream().map(UserMapper::toDTO).collect(Collectors.toList());
        return ResponseEntity.ok(new ApiResponse<>(200, "Succès", dtos));
    }

    /**
     * Update profil : accepte UserDTO dans le body, id path param forcé.
     * - id ne peut pas être modifié
     * - type ne peut pas être modifié via cet endpoint (admin endpoint séparé)
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

    /**
     * Admin-only change role endpoint (example).
     */
    @PostMapping("/{id}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> changeRole(@PathVariable String id, @RequestParam String role) throws Exception {
        authService.updateUserRole(id, role);
        return ResponseEntity.ok(new ApiResponse<>(200, "Role mis à jour", null));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable String id) throws Exception {
        authService.deleteUser(id);
        return ResponseEntity.ok(new ApiResponse<>(200, "Utilisateur supprimé", null));
    }
}
