package com.codencanvas.url_shortener.repository.shard;

import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ShardRouter {

    private final List<JdbcTemplate> shardJdbcTemplates;

    private JdbcTemplate getShardFor(String shortCode) {
        int index = Math.abs(shortCode.hashCode())
                % shardJdbcTemplates.size();

        log.debug("Routing '{}' to shard {}", shortCode, index);
        return shardJdbcTemplates.get(index);
    }

    public void save(String shortCode, String longUrl) {
        JdbcTemplate shard = getShardFor(shortCode);

        shard.update("""
                INSERT INTO urls (short_code, long_url, created_at)
                VALUES (?, ?, NOW())
                """,
                shortCode,
                longUrl);
    }

    public Optional<String> findByShortCode(String shortCode) {
        JdbcTemplate shard = getShardFor(shortCode);

        try {
            String longUrl = shard.queryForObject("""
                    SELECT long_url
                    FROM   urls
                    WHERE  short_code = ?
                    """,
                    String.class,
                    shortCode);
            return Optional.ofNullable(longUrl);

        } catch (Exception e) {
            return Optional.empty();
        }
    }

}
