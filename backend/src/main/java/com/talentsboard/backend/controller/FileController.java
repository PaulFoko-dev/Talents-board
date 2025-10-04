package com.talentsboard.backend.controller;

import com.talentsboard.backend.dto.ApiResponse;
import com.talentsboard.backend.model.User;
import com.talentsboard.backend.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {

    private final FileStorageService storageService;
    private final PdfSkillExtractor extractor;
    private final CvAuditService auditService;
    private final UserService userService;
    private final TicketService ticketService;

    private String currentUid() {
        Authentication a = SecurityContextHolder.getContext().getAuthentication();
        return a == null ? null : String.valueOf(a.getPrincipal());
    }

    private boolean isAdmin(Authentication authentication) {
        for (GrantedAuthority authority : authentication.getAuthorities()) {
            String auth = authority.getAuthority();
            if ("ROLE_ADMIN".equals(auth) || "ROLE_SUPER_ADMIN".equals(auth)) return true;
        }
        return false;
    }

    /**
     * Upload et sauvegarde permanente du CV pour l'utilisateur connecté.
     * Seuls les candidats peuvent avoir un CV.
     */
    @PostMapping(value="/cv", consumes = {"multipart/form-data"})
    public ResponseEntity<ApiResponse<Map<String, String>>> uploadCv(
            @RequestParam(value = "file") MultipartFile file,
            Authentication authentication) {
        try {
            String userId = currentUid();
            if (userId == null)
                return ResponseEntity.status(401).body(new ApiResponse<>(401, "Non authentifié", null));

            User user = userService.getUserById(userId);
            if (user == null)
                return ResponseEntity.status(404).body(new ApiResponse<>(404, "Utilisateur introuvable", null));

            if (user.getType() == null || !"CANDIDAT".equalsIgnoreCase(user.getType().name())) {
                return ResponseEntity.badRequest().body(new ApiResponse<>(400, "Seuls les candidats peuvent avoir un CV", null));
            }

            // Validation du fichier
            storageService.validateFileIsPdfAndSize(file);

            // Sauvegarde
            String cvPath = storageService.storeCv(file, user.getId());
            user.setCvPath(cvPath);
            userService.updateUser(user);

            auditService.logAccess(userId, userId, user.getType().name(),"UPLOAD");

            return ResponseEntity.ok(new ApiResponse<>(200, "CV uploadé avec succès", Map.of("cvPath", cvPath)));
        } catch (Exception e) {
            log.error("Erreur upload CV", e);
            return ResponseEntity.internalServerError().body(new ApiResponse<>(500, "Erreur upload CV : " + e.getMessage(), null));
        }
    }

    /**
     * Mise à jour (remplace) du CV de l'utilisateur connecté.
     */
    @PutMapping(value="/cv", consumes = {"multipart/form-data"})
    public ResponseEntity<ApiResponse<Map<String, String>>> updateCv(
            @RequestParam(value = "file") MultipartFile file,
            Authentication authentication) {
        try {
            ResponseEntity<ApiResponse<Map<String, String>>> response = uploadCv(file, authentication);
            ApiResponse<Map<String, String>> body = response.getBody();
            if (body != null && body.getStatus() == 200) {
                body.setMessage("CV mis à jour avec succès");
            }
            return response;
        } catch (Exception e) {
            log.error("Erreur update CV", e);
            return ResponseEntity.internalServerError().body(new ApiResponse<>(500, "Erreur update CV : " + e.getMessage(), null));
        }
    }

    /**
     * Suppression du CV de l'utilisateur connecté.
     */
    @DeleteMapping("/cv")
    public ResponseEntity<ApiResponse<String>> deleteCv(Authentication authentication) {
        try {
            String userId = currentUid();
            if (userId == null)
                return ResponseEntity.status(401).body(new ApiResponse<>(401, "Non authentifié", null));

            User user = userService.getUserById(userId);
            if (user == null)
                return ResponseEntity.status(404).body(new ApiResponse<>(404, "Utilisateur introuvable", null));

            String cvPath = user.getCvPath();
            if (cvPath == null)
                return ResponseEntity.badRequest().body(new ApiResponse<>(400, "Aucun CV à supprimer", null));

            boolean deleted = storageService.deleteCv(cvPath);
            if (deleted) {
                user.setCvPath(null);
                userService.updateUser(user);
                auditService.logAccess(userId, userId, user.getType().name(),"DELETE");
                return ResponseEntity.ok(new ApiResponse<>(200, "CV supprimé avec succès", null));
            } else {
                return ResponseEntity.internalServerError().body(new ApiResponse<>(500, "Impossible de supprimer le CV", null));
            }
        } catch (Exception e) {
            log.error("Erreur suppression CV", e);
            return ResponseEntity.internalServerError().body(new ApiResponse<>(500, "Erreur suppression CV : " + e.getMessage(), null));
        }
    }

    @PreAuthorize("hasAnyRole('ENTREPRISE','CANDIDAT')")
    @GetMapping("/cv/{userId}")
    public ResponseEntity<Resource> getCv(
            @PathVariable String userId,
            Authentication authentication) throws Exception {

        // Récupération de l'utilisateur connecté
        String actorUid = currentUid();

        User candidate = userService.getUserById(userId);
        String cvPath = candidate.getCvPath();

        User actor = userService.getUserById(actorUid);

        // Vérification d’autorisation
        boolean isOwner = actorUid.equals(userId);
        boolean isEntreprise = actor.getType().name().equals("ENTREPRISE");

        if (!(isOwner || isEntreprise)) {
            throw new org.springframework.security.access.AccessDeniedException("Accès interdit au CV.");
        }

        // Préparer le fichier
        InputStream cvStream = storageService.getCvStream(cvPath);
        InputStreamResource resource = new InputStreamResource(cvStream);

        String ownerName = candidate.getPrenom() + "_" + candidate.getNom().toUpperCase();
        String safeFilename = URLEncoder.encode(ownerName.replaceAll("\\s+", "_"),
                StandardCharsets.UTF_8.toString()) + ".pdf";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + safeFilename + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(resource);
    }


//    /**
//     * Téléchargement/stream du CV d'un utilisateur (par son userId).
//     * Accès autorisé si :
//     * - Le demandeur est le propriétaire
//     * - OU ADMIN/SUPER_ADMIN
//     * - OU ENTREPRISE avec un ticket lié au candidat
//     */
//    @PreAuthorize("hasAnyRole('ADMIN','ENTREPRISE','CANDIDAT')")
//    @GetMapping("/cv/{userId}")
//    public ResponseEntity<?> getCv(
//            @PathVariable String userId,
//            Authentication authentication) {
//
//        try {
//            String actorUid = currentUid();
//            // Le check 401 n'est plus strictement nécessaire grâce à @PreAuthorize/SecurityConfig,
//            // mais le laisser est une bonne redondance. Assurez-vous d'utiliser votre ApiResponse.
//            if (actorUid == null) {
//                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .body(new ApiResponse<>(HttpStatus.UNAUTHORIZED.value(), "Non authentifié", null));
//            }
//
//            User candidate = userService.getUserById(userId);
//            if (candidate == null || candidate.getCvPath() == null) {
//                return ResponseEntity.status(HttpStatus.NOT_FOUND)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .body(new ApiResponse<>(HttpStatus.NOT_FOUND.value(), "CV/Utilisateur introuvable", null));
//            }
//
//            String cvPath = candidate.getCvPath();
//
//            // Vérification autorisation (inchangée)
//            boolean authorized = false;
//            if (actorUid.equals(userId)) authorized = true;
//            if (isAdmin(authentication)) authorized = true;
//            // ... (Logique Entreprise Match) ...
//
//            if (!authorized) {
//                // Renvoyer JSON 403
//                return ResponseEntity.status(HttpStatus.FORBIDDEN)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .body(new ApiResponse<>(HttpStatus.FORBIDDEN.value(), "Accès interdit au CV", null));
//            }
//
//            // --- SECTION DE SUCCÈS (PAS DE COMMIT AVANT CE POINT) ---
//
//            // 1. Récupère le flux du fichier (peut lever IOException/FileNotFound)
//            InputStream cvStream = storageService.getCvStream(cvPath);
//
//            // 2. Audit log
//            User actorUser = userService.getUserById(actorUid);
//            auditService.logAccess(userId, actorUid,
//                    (actorUser != null && actorUser.getType() != null) ? actorUser.getType().name() : "INCONNU",
//                    "DOWNLOAD");
//
//            String ownerName = candidate.getPrenom() + "_" + candidate.getNom().toUpperCase(); // Exemple : John_Doe
//            String safeFilename = URLEncoder.encode(ownerName.replaceAll("\\s+", "_"), StandardCharsets.UTF_8.toString()) + ".pdf";
//
//            // 3. Streaming du PDF (Le corps de la réponse sera le stream)
//            StreamingResponseBody stream = outputStream -> {
//                try (InputStream in = cvStream) {
//                    byte[] buffer = new byte[8192];
//                    int n;
//                    while ((n = in.read(buffer)) != -1) {
//                        outputStream.write(buffer, 0, n);
//                    }
//                }
//            };
//
//            // Renvoyer le Stream (200 OK)
//            return ResponseEntity.ok()
//                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"cv_" + safeFilename + "\"")
//                    .contentType(MediaType.APPLICATION_PDF)
//                    .body(stream); // Renvoyer le stream directement
//
//        } catch (Exception e) {
//            // Gère les exceptions de bas niveau (IO/File Not Found)
//            log.error("Erreur de lecture/streaming du CV", e);
//            return ResponseEntity.internalServerError()
//                    .contentType(MediaType.APPLICATION_JSON) // Rétablir le Content-Type pour le JSON
//                    .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Erreur lors de la lecture du CV : " + e.getMessage(), null));
//        }
//    }


}
