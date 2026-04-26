package com.codencanvas.url_shortener.config;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class ShardDataSourceConfig {

    private final AppProperties appProperties;

    @Bean
    public List<DataSource> shardDataSources() {
        List<AppProperties.DbConfig> configs = appProperties.getDatasource().getShards();

        List<DataSource> dataSources = new ArrayList<>();

        for (int i = 0; i < configs.size(); i++) {
            dataSources.add(buildDataSource(configs.get(i), i));
        }

        return dataSources;
    }

    @Bean
    public List<JdbcTemplate> shardJdbcTemplates() {
        List<JdbcTemplate> templates = new ArrayList<>();

        for (DataSource dataSource : shardDataSources()) {
            templates.add(new JdbcTemplate(dataSource));
        }

        return templates;
    }

    @Bean
    public PlatformTransactionManager shardTransactionManager() {
        return new DataSourceTransactionManager(
                shardDataSources().get(0));
    }

    private DataSource buildDataSource(AppProperties.DbConfig config, int index) {
        HikariConfig hikari = new HikariConfig();
        hikari.setJdbcUrl(config.getUrl());
        hikari.setUsername(config.getUsername());
        hikari.setPassword(config.getPassword());
        hikari.setDriverClassName(config.getDriverClassName());
        hikari.setMaximumPoolSize(config.getPool().getMaximumPoolSize());
        hikari.setMinimumIdle(config.getPool().getMinimumIdle());
        hikari.setConnectionTimeout(config.getPool().getConnectionTimeout());
        hikari.setPoolName("Shard-" + index + "-Pool");

        return new HikariDataSource(hikari);
    }
}
