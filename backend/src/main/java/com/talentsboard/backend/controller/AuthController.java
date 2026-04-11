package com.talentsboard.backend.controller;

import com.talentsboard.backend.dto.UserDTO;
import com.talentsboard.backend.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Auth", description = "Authentification et gestion du profil")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Enregistrer un utilisateur après authentification Firebase")
    public ResponseEntity<UserDTO> register(@RequestBody UserDTO dto) {
        return ResponseEntity.ok(authService.registerUser(dto));
    }

    @GetMapping("/profile/{uid}")
    @Operation(summary = "Obtenir le profil de l'utilisateur connecté")
    public ResponseEntity<UserDTO> getProfile(@PathVariable String uid) {
        return ResponseEntity.ok(authService.getUserProfile(uid));
    }
}
