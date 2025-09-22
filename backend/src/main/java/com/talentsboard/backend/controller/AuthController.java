package com.talentsboard.backend.controller;

import com.talentsboard.backend.dto.*;
import com.talentsboard.backend.model.*;
import com.talentsboard.backend.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller REST pour l’authentification et l’inscription.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Endpoint pour inscrire un candidat.
     * Exemple :
     * curl -X POST http://localhost:8080/api/auth/register/candidat \
     *   -H "Content-Type: application/json" \
     *   -d '{"nom":"Doe","prenom":"John","email":"john@example.com","motDePasse":"password"}'
     */
    @PostMapping("/register/candidat")
    public ResponseEntity<ApiResponse<User>> registerCandidat(@RequestBody @Valid RegisterCandidatRequest request) throws Exception {
        Candidat candidat = new Candidat();
        candidat.setNom(request.getNom());
        candidat.setPrenom(request.getPrenom());
        candidat.setEmail(request.getEmail());

        User newUser = authService.register(candidat, request.getMotDePasse());
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Inscription réussie", newUser));
    }

    /**
     * Endpoint pour inscrire une entreprise.
     * Exemple :
     * curl -X POST http://localhost:8080/api/auth/register/entreprise \
     *   -H "Content-Type: application/json" \
     *   -d '{"nom":"TechCorp","email":"contact@techcorp.com","motDePasse":"securepwd","secteur":"IT","localisation":"Paris","siteWeb":"https://techcorp.com"}'
     */
    @PostMapping("/register/entreprise")
    public ResponseEntity<ApiResponse<User>> registerEntreprise(@RequestBody @Valid RegisterEntrepriseRequest request) throws Exception {
        Entreprise entreprise = new Entreprise();
        entreprise.setNom(request.getNom());
        entreprise.setEmail(request.getEmail());
        entreprise.setSecteur(request.getSecteur());
        entreprise.setLocalisation(request.getLocalisation());
        entreprise.setSiteWeb(request.getSiteWeb());

        User newUser = authService.register(entreprise, request.getMotDePasse());
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Inscription réussie", newUser));
    }

    /**
     * Endpoint pour se connecter.
     * Exemple :
     * curl -X POST http://localhost:8080/api/auth/login \
     *   -H "Content-Type: application/json" \
     *   -d '{"email":"john@example.com","motDePasse":"password"}'
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@RequestBody @Valid AuthRequest request) throws Exception {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Connexion réussie", response));
    }
}
