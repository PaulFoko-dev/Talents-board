package com.talentsboard.backend.exception;

public class ForbiddenException extends RuntimeException {
    public ForbiddenException() { super("Forbidden"); }
    public ForbiddenException(String message) { super(message); }
}
