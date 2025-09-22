package com.talentsboard.backend.exception;

/**
 * Exception levée quand un email est déjà utilisé.
 */
public class EmailAlreadyExistsException extends RuntimeException {
    public EmailAlreadyExistsException(String message) {
        super(message);
    }
}
