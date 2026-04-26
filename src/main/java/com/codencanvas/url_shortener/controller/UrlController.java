package com.codencanvas.url_shortener.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codencanvas.url_shortener.dto.ShortenRequest;
import com.codencanvas.url_shortener.dto.ShortenResponse;
import com.codencanvas.url_shortener.service.RateLimiterService;
import com.codencanvas.url_shortener.service.UrlShortenerService;
import com.codencanvas.url_shortener.service.UrlValidatorService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class UrlController {

    private final RateLimiterService rateLimiterService;
    private final UrlValidatorService urlValidatorService;
    private final UrlShortenerService urlShortenerService;

    @PostMapping(path = "/api/shorten", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ShortenResponse> shortenJson(
            @Valid @RequestBody ShortenRequest request,
            HttpServletRequest httpRequest) {

        return shortenInternal(request.longUrl(), httpRequest);
    }

    @PostMapping(path = "/api/shorten", consumes = {
            MediaType.MULTIPART_FORM_DATA_VALUE,
            MediaType.APPLICATION_FORM_URLENCODED_VALUE
    })
    public ResponseEntity<ShortenResponse> shortenForm(
            @RequestParam("longUrl") String longUrl,
            HttpServletRequest httpRequest) {

        return shortenInternal(longUrl, httpRequest);
    }

    @GetMapping("/{shortCode}")
    public ResponseEntity<Void> redirect(
            @PathVariable String shortCode) {

        String longUrl = urlShortenerService.resolve(shortCode);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.LOCATION, longUrl);

        return ResponseEntity
                .status(HttpStatus.FOUND) // 302
                .headers(headers)
                .build();
    }

    private ResponseEntity<ShortenResponse> shortenInternal(
            String longUrl,
            HttpServletRequest httpRequest) {

        String ip = httpRequest.getRemoteAddr();
        rateLimiterService.checkLimit(ip);

        urlValidatorService.validate(longUrl);

        String shortUrl = urlShortenerService.shorten(longUrl);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ShortenResponse(shortUrl));
    }

}
