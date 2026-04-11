package com.talentsboard.backend.controller;

import com.talentsboard.backend.dto.TicketDTO;
import com.talentsboard.backend.model.Ticket;
import com.talentsboard.backend.service.TicketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Tickets", description = "Gestion des tickets de recrutement")
public class TicketController {

    private final TicketService ticketService;

    @GetMapping
    @Operation(summary = "Lister tous les tickets")
    public ResponseEntity<List<TicketDTO>> getAll() {
        return ResponseEntity.ok(ticketService.getAllTickets());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtenir un ticket par ID")
    public ResponseEntity<TicketDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ticketService.getTicketById(id));
    }

    @GetMapping("/owner/{ownerId}")
    @Operation(summary = "Tickets d'un utilisateur")
    public ResponseEntity<List<TicketDTO>> getByOwner(@PathVariable Long ownerId) {
        return ResponseEntity.ok(ticketService.getTicketsByOwner(ownerId));
    }

    @GetMapping("/type/{type}")
    @Operation(summary = "Tickets ouverts par type (SEARCH_TALENT ou SEARCH_OPPORTUNITY)")
    public ResponseEntity<List<TicketDTO>> getByType(@PathVariable Ticket.TicketType type) {
        return ResponseEntity.ok(ticketService.getOpenTicketsByType(type));
    }

    @PostMapping
    @Operation(summary = "Créer un ticket")
    public ResponseEntity<TicketDTO> create(@RequestBody TicketDTO dto) {
        return ResponseEntity.ok(ticketService.createTicket(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour un ticket")
    public ResponseEntity<TicketDTO> update(@PathVariable Long id, @RequestBody TicketDTO dto) {
        return ResponseEntity.ok(ticketService.updateTicket(id, dto));
    }

    @PatchMapping("/{id}/close")
    @Operation(summary = "Fermer un ticket")
    public ResponseEntity<TicketDTO> close(@PathVariable Long id) {
        return ResponseEntity.ok(ticketService.closeTicket(id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un ticket")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        ticketService.deleteTicket(id);
        return ResponseEntity.noContent().build();
    }
}
