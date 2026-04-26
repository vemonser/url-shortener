package com.codencanvas.url_shortener.service;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.codencanvas.url_shortener.config.AppProperties;
import com.codencanvas.url_shortener.model.Key;
import com.codencanvas.url_shortener.repository.KeyRepository;

import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class KeyGeneratorService {
    private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyz" +
            "ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
            "0123456789";

    private static final SecureRandom RANDOM = new SecureRandom();

    private final KeyRepository keyRepository;
    private final AppProperties appProperties;

    @PostConstruct
    public void initializeKeys() {
        long availableKeys = keyRepository.countByIsMappedFalse();
        int needed = appProperties.getShortener().getPreGenCount();

        if (availableKeys < needed) {
            long toGenerate = needed - availableKeys;
            log.info("Generating {} keys on startup...", toGenerate);
            generateAndSave(Math.toIntExact(toGenerate));
            log.info("Key generation complete.");
        }
    }

    @Scheduled(fixedDelay = 300_000)
    public void refillKeys() {
        long available = keyRepository.countByIsMappedFalse();
        int threshold = appProperties.getShortener().getRefillThreshold();

        if (available < threshold) {
            int toGenerate = appProperties.getShortener().getPreGenCount();
            log.info("Keys running low ({}). Refilling {}...", available, toGenerate);
            generateAndSave(toGenerate);
        }
    }

    @Transactional
    public void generateAndSave(int count) {
        List<Key> keys = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            keys.add(Key.builder()
                    .shortCode(generateCode())
                    .build());

            if (keys.size() == 1000) {
                keyRepository.saveAll(keys);
                keys.clear();
            }
        }

        if (!keys.isEmpty()) {
            keyRepository.saveAll(keys);
        }
    }

    private String generateCode() {
        StringBuilder code = new StringBuilder(8);

        for (int i = 0; i < 8; i++) {
            code.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
        }

        return code.toString();
    }
}
