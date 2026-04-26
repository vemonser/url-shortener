package com.codencanvas.url_shortener.model;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Url {

    private Long id;

    private String shortCode;

    private String longUrl;

    private LocalDateTime createdAt;
}