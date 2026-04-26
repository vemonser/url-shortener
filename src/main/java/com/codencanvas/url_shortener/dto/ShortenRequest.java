package com.codencanvas.url_shortener.dto;

import jakarta.validation.constraints.NotBlank;

public record ShortenRequest(
        @NotBlank(message = "URL must not be blank") String longUrl) {
}