package com.vaticano.paroquia.exception;

public class ForbiddenException extends RuntimeException {
    public ForbiddenException(String message) {
        super(message);
    }

    public ForbiddenException() {
        super("Acesso negado. Você não tem permissão para acessar este recurso.");
    }
}
