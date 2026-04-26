package com.codencanvas.url_shortener.exception;

public class NoKeysAvailableException extends RuntimeException {
    public NoKeysAvailableException() {
        super("No keys available. Please try again later.");
    }

    public NoKeysAvailableException(String message) {
        super(message);
    }
}
