package com.codencanvas.url_shortener.service;

import java.time.Duration;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.codencanvas.url_shortener.config.AppProperties;
import com.codencanvas.url_shortener.exception.RateLimitException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RateLimiterService {

    private final RedisTemplate<String, String> redisTemplate;
    private final AppProperties appProperties;

    private static final String PREFIX = "rate:";

    public void checkLimit(String ip) {
        String key = PREFIX + ip;
        int max = appProperties.getRedis().getRateLimitMax();
        int window = appProperties.getRedis().getRateLimitWindow();

        Long count = redisTemplate.opsForValue().increment(key);

        if (count == null) {
            throw new RateLimitException(ip);
        }

        if (count == 1) {
            redisTemplate.expire(key, Duration.ofSeconds(window));
        }

        if (count > max) {
            throw new RateLimitException(ip);
        }
    }
}
