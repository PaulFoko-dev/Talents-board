package com.talentsboard.backend.controller;

import com.talentsboard.backend.dto.ApplicationDTO;
import com.talentsboard.backend.model.Application;
import com.talentsboard.backend.service.ApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Applications", description = "Gestion des candidatures")
public class ApplicationController {

    private final ApplicationService applicationService;

    @GetMapping
    @Operation(summary = "Lister toutes les candidatures")
    public ResponseEntity<List<ApplicationDTO>> getAll() {
        return ResponseEntity.ok(applicationService.getAllApplications());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtenir une candidature par ID")
    public ResponseEntity<ApplicationDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(applicationService.getApplicationById(id));
    }

    @GetMapping("/candidate/{candidateId}")
    @Operation(summary = "Candidatures d'un candidat")
    public ResponseEntity<List<ApplicationDTO>> getByCandidate(@PathVariable Long candidateId) {
        return ResponseEntity.ok(applicationService.getApplicationsByCandidate(candidateId));
    }

    @GetMapping("/ticket/{ticketId}")
    @Operation(summary = "Candidatures pour un ticket")
    public ResponseEntity<List<ApplicationDTO>> getByTicket(@PathVariable Long ticketId) {
        return ResponseEntity.ok(applicationService.getApplicationsByTicket(ticketId));
    }

    @PostMapping
    @Operation(summary = "Postuler à un ticket")
    public ResponseEntity<ApplicationDTO> create(@RequestBody ApplicationDTO dto) {
        return ResponseEntity.ok(applicationService.createApplication(dto));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Mettre à jour le statut d'une candidature (PENDING/ACCEPTED/REJECTED)")
    public ResponseEntity<ApplicationDTO> updateStatus(@PathVariable Long id, @RequestParam Application.ApplicationStatus status) {
        return ResponseEntity.ok(applicationService.updateStatus(id, status));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer une candidature")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        applicationService.deleteApplication(id);
        return ResponseEntity.noContent().build();
    }
}
