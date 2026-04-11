package com.talentsboard.backend.controller;

import com.talentsboard.backend.dto.UserDTO;
import com.talentsboard.backend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Users", description = "Gestion des utilisateurs")
public class UserController {

    private final UserService userService;

    @GetMapping
    @Operation(summary = "Lister tous les utilisateurs")
    public ResponseEntity<List<UserDTO>> getAll() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtenir un utilisateur par ID")
    public ResponseEntity<UserDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping("/firebase/{uid}")
    @Operation(summary = "Obtenir un utilisateur par Firebase UID")
    public ResponseEntity<UserDTO> getByFirebaseUid(@PathVariable String uid) {
        return ResponseEntity.ok(userService.getUserByFirebaseUid(uid));
    }

    @GetMapping("/candidates")
    @Operation(summary = "Lister tous les candidats")
    public ResponseEntity<List<UserDTO>> getCandidates() {
        return ResponseEntity.ok(userService.getCandidates());
    }

    @GetMapping("/enterprises")
    @Operation(summary = "Lister toutes les entreprises")
    public ResponseEntity<List<UserDTO>> getEnterprises() {
        return ResponseEntity.ok(userService.getEnterprises());
    }

    @PostMapping
    @Operation(summary = "Créer un utilisateur")
    public ResponseEntity<UserDTO> create(@RequestBody UserDTO dto) {
        return ResponseEntity.ok(userService.createUser(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour un utilisateur")
    public ResponseEntity<UserDTO> update(@PathVariable Long id, @RequestBody UserDTO dto) {
        return ResponseEntity.ok(userService.updateUser(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un utilisateur")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
