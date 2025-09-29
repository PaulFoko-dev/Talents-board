package com.talentsboard.backend.controller;

import com.talentsboard.backend.dto.*;
import com.talentsboard.backend.mapper.UserMapper;
import com.talentsboard.backend.model.User;
import com.talentsboard.backend.model.UserType;
import com.talentsboard.backend.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Endpoints Auth :
 * - /register/candidat
 * - /register/entreprise
 * - /login (email+password)
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) { this.authService = authService; }

    @PostMapping("/register/candidat")
    public ResponseEntity<ApiResponse<UserDTO>> registerCandidat(@RequestBody @Valid RegisterCandidatRequest req) throws Exception {
        User u = new User();
        u.setNom(req.getNom());
        u.setPrenom(req.getPrenom());
        u.setEmail(req.getEmail());
        u.setType(UserType.CANDIDAT);

        User saved = authService.register(u, req.getMotDePasse());
        return ResponseEntity.ok(new ApiResponse<>(200, "Inscription réussie", UserMapper.toDTO(saved)));
    }

    @PostMapping("/register/entreprise")
    public ResponseEntity<ApiResponse<UserDTO>> registerEntreprise(@RequestBody @Valid RegisterEntrepriseRequest req) throws Exception {
        User u = new User();
        u.setNom(req.getNom());
        u.setEmail(req.getEmail());
        u.setSecteur(req.getSecteur());
        u.setLocalisation(req.getLocalisation());
        u.setSiteWeb(req.getSiteWeb());
        u.setType(UserType.ENTREPRISE);

        User saved = authService.register(u, req.getMotDePasse());
        return ResponseEntity.ok(new ApiResponse<>(200, "Inscription entreprise réussie", UserMapper.toDTO(saved)));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@RequestBody @Valid AuthRequest req) {
        AuthResponse resp = authService.login(req);
        return ResponseEntity.ok(new ApiResponse<>(200, "Connexion réussie", resp));
    }
}
