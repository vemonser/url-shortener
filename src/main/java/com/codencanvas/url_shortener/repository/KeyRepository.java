package com.codencanvas.url_shortener.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.codencanvas.url_shortener.model.Key;

import jakarta.transaction.Transactional;

@Repository
public interface KeyRepository extends JpaRepository<Key, Long> {

    long countByIsMappedFalse();

    @Transactional
    @Query(value = """
            UPDATE short_keys 
            SET    is_mapped = true
            WHERE  short_code = (
                SELECT short_code
                FROM   short_keys 
                WHERE  is_mapped = false
                LIMIT  1
                FOR UPDATE SKIP LOCKED
            )
            RETURNING short_code
            """, nativeQuery = true)
    Optional<String> findAndClaimKey();

}
