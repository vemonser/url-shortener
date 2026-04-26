package com.codencanvas.url_shortener.exception;

public class RateLimitException extends RuntimeException {
    public RateLimitException(String ip) {
        super("Too many requests from: " + ip);
    }
}