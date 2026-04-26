package com.codencanvas.url_shortener.exception;

public class UrlNotFoundException extends RuntimeException {
    public UrlNotFoundException(String shortCode) {
        super("Short code not found: " + shortCode);
    }
}