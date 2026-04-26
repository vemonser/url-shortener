package com.codencanvas.url_shortener.service;

import java.time.Duration;
import java.util.Optional;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.codencanvas.url_shortener.config.AppProperties;
import com.codencanvas.url_shortener.exception.NoKeysAvailableException;
import com.codencanvas.url_shortener.exception.UrlNotFoundException;
import com.codencanvas.url_shortener.repository.KeyRepository;
import com.codencanvas.url_shortener.repository.shard.ShardRouter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UrlShortenerService {
    private final KeyRepository keyRepository;
    private final ShardRouter shardRouter;
    private final RedisTemplate<String, String> redisTemplate;
    private final AppProperties appProperties;

    @Transactional("primaryTransactionManager")
    public String shorten(String longUrl) {

        Optional<String> shortCode = keyRepository.findAndClaimKey();

        if (shortCode.isEmpty()) {
            throw new NoKeysAvailableException("No keys available. Try again later.");
        }

        String code = shortCode.get();

        shardRouter.save(code, longUrl);

        redisTemplate.opsForValue().set(
                code,
                longUrl,
                Duration.ofSeconds(appProperties.getRedis().getTtlSeconds()));

        return appProperties.getShortener().getBaseUrl() + "/" + code;
    }

    public String resolve(String shortCode) {

        String cached = redisTemplate.opsForValue().get(shortCode);
        if (cached != null) {
            log.debug("Cache hit for: {}", shortCode);
            return cached; // ~1ms ✅
        }

        log.debug("Cache miss for: {}", shortCode);
        Optional<String> longUrl = shardRouter.findByShortCode(shortCode);

        if (longUrl.isEmpty()) {
            throw new UrlNotFoundException(shortCode);
        }

        redisTemplate.opsForValue().set(
                shortCode,
                longUrl.get(),
                Duration.ofSeconds(appProperties.getRedis().getTtlSeconds()));

        return longUrl.get();
    }
}
