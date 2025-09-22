package com.talentsboard.backend.exception;

import com.talentsboard.backend.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Gestionnaire global des exceptions.
 * Permet d’uniformiser les erreurs dans toutes les réponses API.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Exemple d’exception personnalisée : Email déjà utilisé
     */
    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ApiResponse<Object>> handleEmailExists(EmailAlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ApiResponse<>(409, ex.getMessage(), null));
    }

    /**
     * Erreurs génériques (par défaut → 500).
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGeneric(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(500, "Erreur serveur : " + ex.getMessage(), null));
    }
}
