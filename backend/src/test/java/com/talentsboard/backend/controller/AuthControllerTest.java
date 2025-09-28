package com.talentsboard.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.talentsboard.backend.dto.AuthRequest;
import com.talentsboard.backend.dto.AuthResponse;
import com.talentsboard.backend.dto.RegisterCandidatRequest;
import com.talentsboard.backend.dto.RegisterEntrepriseRequest;
import com.talentsboard.backend.model.Candidat;
import com.talentsboard.backend.model.Entreprise;
import com.talentsboard.backend.model.User;
import com.talentsboard.backend.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests unitaires pour AuthController.
 * Vérifie les endpoints d'inscription (candidat/entreprise) et de login.
 */
@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testRegisterCandidat() throws Exception {
        RegisterCandidatRequest request = new RegisterCandidatRequest();
        request.setNom("Doe");
        request.setPrenom("Alice");
        request.setEmail("alice@example.com");
        request.setMotDePasse("password");

        User savedUser = new Candidat();
        savedUser.setId("uid123");
        savedUser.setNom("Doe");
        ((Candidat) savedUser).setPrenom("Alice");
        savedUser.setEmail("alice@example.com");

        when(authService.register(any(User.class), eq("password")))
                .thenReturn(savedUser);

        mockMvc.perform(post("/api/auth/register/candidat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("uid123"))
                .andExpect(jsonPath("$.nom").value("Doe"))
                .andExpect(jsonPath("$.prenom").value("Alice"));
    }

    @Test
    void testRegisterEntreprise() throws Exception {
        RegisterEntrepriseRequest request = new RegisterEntrepriseRequest();
        request.setNom("TechCorp");
        request.setEmail("contact@techcorp.com");
        request.setMotDePasse("securepwd");
        request.setSecteur("IT");
        request.setLocalisation("Paris");
        request.setSiteWeb("https://techcorp.com");

        User savedUser = new Entreprise();
        savedUser.setId("uid456");
        savedUser.setNom("TechCorp");
        savedUser.setEmail("contact@techcorp.com");
        ((Entreprise) savedUser).setSecteur("IT");
        ((Entreprise) savedUser).setLocalisation("Paris");
        ((Entreprise) savedUser).setSiteWeb("https://techcorp.com");

        when(authService.register(any(User.class), eq("securepwd")))
                .thenReturn(savedUser);

        mockMvc.perform(post("/api/auth/register/entreprise")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("uid456"))
                .andExpect(jsonPath("$.nom").value("TechCorp"))
                .andExpect(jsonPath("$.secteur").value("IT"))
                .andExpect(jsonPath("$.localisation").value("Paris"))
                .andExpect(jsonPath("$.siteWeb").value("https://techcorp.com"));
    }

    @Test
    void testLogin() throws Exception {
        AuthRequest request = new AuthRequest("alice@example.com", "password");
        AuthResponse response = new AuthResponse("fake-token-123", "uid123");

        when(authService.login(any(AuthRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("fake-token-123"))
                .andExpect(jsonPath("$.userId").value("uid123"));
    }
}
