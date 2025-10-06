package com.talentsboard.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.talentsboard.backend.dto.*;
import com.talentsboard.backend.mapper.TicketMapper;
import com.talentsboard.backend.model.Ticket;
import com.talentsboard.backend.service.AdvancedMatchService;
import com.talentsboard.backend.service.FileStorageService;
import com.talentsboard.backend.service.PdfSkillExtractor;
import com.talentsboard.backend.service.TicketService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import static com.talentsboard.backend.mapper.TicketMapper.toDTO;

/**
 * Tickets API : create (candidat/entreprise), validate, CRUD, matches.
 */
@RestController
@AllArgsConstructor
@RequestMapping("/api/tickets")
public class TicketController {

    private final TicketService ticketService;
    private final AdvancedMatchService advancedMatchService;
    private final PdfSkillExtractor pdfSkillExtractor;
    private final FileStorageService fileStorageService;

    private String currentUid() {
        Authentication a = SecurityContextHolder.getContext().getAuthentication();
        return a == null ? null : String.valueOf(a.getPrincipal());
    }


    @PostMapping(value = "/candidat", consumes = {"multipart/form-data"})
    public ResponseEntity<ApiResponse<TicketDTO>> createCandidat(
            @RequestPart("data") String data, // reçu comme String (Swagger ou Axios)
            @RequestPart(value = "file", required = false) MultipartFile file
    ) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        CreateTicketCandidatRequest req = mapper.readValue(data, CreateTicketCandidatRequest.class);

        String uid = currentUid();

        Map<String, Object> extracted = null;
        if (file != null && !file.isEmpty()) {
            // validate file (size, magic, etc.)
            fileStorageService.validateFileIsPdfAndSize(file);
            try (InputStream in = file.getInputStream()) {
                extracted = pdfSkillExtractor.extract(in);
            }
        }

        Ticket t = ticketService.createCandidatTicket(req, uid, extracted);
        TicketDTO dto = toDTO(t);
        return ResponseEntity.ok(new ApiResponse<>(200, "Ticket draft créé", dto));
    }

    @PostMapping("/entreprise")
    public ResponseEntity<ApiResponse<TicketDTO>> createEntreprise(
            @RequestBody CreateTicketEntrepriseRequest req) throws Exception {

        String uid = currentUid();
        if (uid == null) return ResponseEntity.status(401).body(new ApiResponse<>(401, "Non authentifié", null));

        Ticket t = ticketService.createEntrepriseDraft(req, uid);
        return ResponseEntity.ok(new ApiResponse<>(200, "Ticket draft créé", toDTO(t)));
    }



    @PutMapping("/{id}/validate")
    public ResponseEntity<ApiResponse<TicketDTO>> validateTicket(
            @PathVariable String id,
            @RequestBody ValidateTicketEntrepriseRequest req) throws Exception {

        String uid = currentUid();
        if (uid == null) return ResponseEntity.status(401).body(new ApiResponse<>(401, "Non authentifié", null));

        TicketDTO t = ticketService.validateAndPublishEntreprise(id, req, uid);
        return ResponseEntity.ok(new ApiResponse<>(200, "Ticket publié", t));
    }

    @GetMapping("/published")
    public ResponseEntity<ApiResponse<List<TicketDTO>>> getPublishedTickets(
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(required = false) String pageToken) {
        try {
            List<Ticket> tickets = ticketService.listPublished(limit, pageToken);
            if (tickets.isEmpty()) {
                return ResponseEntity.status(404)
                        .body(new ApiResponse<>(404, "Aucun ticket publié trouvé", null));
            }
            List<TicketDTO> dtoList = tickets.stream()
                    .map(TicketMapper::toDTO)
                    .toList();
            return ResponseEntity.ok(new ApiResponse<>(200, "Succès", dtoList));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(new ApiResponse<>(500, "Erreur serveur: " + e.getMessage(), null));
        }
    }

    @GetMapping("/list")
    public ResponseEntity<ApiResponse<List<TicketDTO>>> getTicketsByOwner(
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(required = false) String pageToken) {
        try {
            String uid = currentUid();
            if (uid == null)
                return ResponseEntity.status(401).body(new ApiResponse<>(401, "Non authentifié", null));

            List<Ticket> tickets = ticketService.listByOwner(uid, limit, pageToken);
            if (tickets.isEmpty()) {
                return ResponseEntity.status(404)
                        .body(new ApiResponse<>(404, "Aucun ticket trouvé pour cet utilisateur", null));
            }
            List<TicketDTO> dtoList = tickets.stream()
                    .map(TicketMapper::toDTO)
                    .toList();
            return ResponseEntity.ok(new ApiResponse<>(200, "Succès", dtoList));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(new ApiResponse<>(500, "Erreur serveur: " + e.getMessage(), null));
        }
    }


    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TicketDTO>> getTicket(@PathVariable String id) throws Exception {
        Ticket t = ticketService.getTicket(id);
        if (t == null) return ResponseEntity.status(404).body(new ApiResponse<>(404, "Introuvable", null));
        return ResponseEntity.ok(new ApiResponse<>(200, "Succès", toDTO(t)));
    }

    @GetMapping("/{id}/matches")
    public ResponseEntity<ApiResponse<List<MatchResultDTO>>> getMatches(@PathVariable String id,
                                                                        @RequestParam(defaultValue = "10") int limit) throws Exception {
        String uid = currentUid();
        Ticket t = ticketService.getTicket(id);

        if (t == null) return ResponseEntity.status(404).body(new ApiResponse<>(404, "Ticket introuvable", null));

        // Vérification de sécurité : seul le propriétaire peut consulter les matches
        if (!t.getOwnerUid().equals(uid)) return ResponseEntity.status(403).body(new ApiResponse<>(403, "Accès refusé. Vous devez être le propriétaire du ticket pour voir ses matches.", null));

        List<MatchResultDTO> matches = advancedMatchService.findMatchesForTicket(t, limit);

        return ResponseEntity.ok(new ApiResponse<>(200, "Succès. Matches trouvés : " + matches.size(), matches));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteTicket(@PathVariable String id) throws Exception {
        String uid = currentUid();
        boolean isAdmin = false; // check authorities if ROLE_ADMIN present
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) isAdmin = true;
        ticketService.deleteTicket(id, uid, isAdmin);
        return ResponseEntity.ok(new ApiResponse<>(200, "Ticket supprimé", null));
    }
}