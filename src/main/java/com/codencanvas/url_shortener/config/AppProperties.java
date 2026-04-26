package com.codencanvas.url_shortener.config;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data                                    
@Component                               
@ConfigurationProperties(prefix = "app") 
public class AppProperties {
 
    // ── DATASOURCE ─────────────────────────────────
    private Datasource datasource = new Datasource();
 
    // ── REDIS ──────────────────────────────────────
    private Redis redis = new Redis();
 
    // ── SHORTENER ──────────────────────────────────
    private Shortener shortener = new Shortener();

    @Data
    public static class Datasource {
        private DbConfig primary = new DbConfig();
        private List<DbConfig> shards;  
    }
 

    @Data
    public static class DbConfig {
        private String url;
        private String username;
        private String password;
        private String driverClassName;  
        private Pool pool = new Pool();
    }

    @Data
    public static class Pool {
        private int maximumPoolSize  = 10;   
        private int minimumIdle      = 2;
        private int connectionTimeout = 2000;
    }
 
    @Data
    public static class Redis {
        private String host            = "localhost";
        private int    port            = 6_379;
        private long   ttlSeconds      = 3_600;
        private int    rateLimitWindow = 60;
        private int    rateLimitMax    = 30;
    }
 
    @Data
    public static class Shortener {
        private int    codeLength      = 8;
        private int    preGenCount     = 100_000;
        private int    refillThreshold = 10_000;
        private String baseUrl         = "http://localhost:8080";
    }
}