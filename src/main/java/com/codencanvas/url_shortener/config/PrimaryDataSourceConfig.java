package com.codencanvas.url_shortener.config;

import javax.sql.DataSource;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
    basePackages = "com.codencanvas.url_shortener.repository",
    entityManagerFactoryRef = "primaryEntityManagerFactory",
    transactionManagerRef   = "primaryTransactionManager"
)
@RequiredArgsConstructor
public class PrimaryDataSourceConfig {

    private final AppProperties appProperties;

    @Bean
    @Primary
    public DataSource primaryDataSource() {
        AppProperties.DbConfig config = appProperties.getDatasource().getPrimary();
        AppProperties.Pool pool = config.getPool();

        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(config.getUrl());
        hikariConfig.setUsername(config.getUsername());
        hikariConfig.setPassword(config.getPassword());
        hikariConfig.setDriverClassName(config.getDriverClassName());
        hikariConfig.setMaximumPoolSize(pool.getMaximumPoolSize());
        hikariConfig.setMinimumIdle(pool.getMinimumIdle());
        hikariConfig.setConnectionTimeout(pool.getConnectionTimeout());
        hikariConfig.setPoolName("PrimaryDB-Pool");

        return new HikariDataSource(hikariConfig);
    }

    @Bean
    @Primary
    public LocalContainerEntityManagerFactoryBean primaryEntityManagerFactory() {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();

        em.setDataSource(primaryDataSource());
        em.setPackagesToScan("com.codencanvas.url_shortener.model");
        em.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        em.setJpaPropertyMap(hibernateProperties());
        em.setPersistenceUnitName("primary");

        return em;
    }

    @Bean
    @Primary
    public PlatformTransactionManager primaryTransactionManager(
            EntityManagerFactory primaryEntityManagerFactory) {

        JpaTransactionManager tm = new JpaTransactionManager();
        tm.setEntityManagerFactory(primaryEntityManagerFactory);
        return tm;
    }

    private Map<String, Object> hibernateProperties() {
        Map<String, Object> props = new HashMap<>();
        props.put("hibernate.dialect",
                "org.hibernate.dialect.PostgreSQLDialect");
        props.put("hibernate.hbm2ddl.auto", "none");
        props.put("hibernate.format_sql", "true");
        return props;
    }
}