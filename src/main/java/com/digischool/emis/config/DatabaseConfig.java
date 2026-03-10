package com.digischool.emis.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Database configuration manager for DigiSchool EMIS.
 * Supports both PostgreSQL and MySQL databases using HikariCP connection pooling.
 */
public class DatabaseConfig {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseConfig.class);
    private static final String PROPERTIES_FILE = "application.properties";

    private static DatabaseConfig instance;
    private final Properties properties;
    private HikariDataSource dataSource;

    private DatabaseConfig() {
        properties = loadProperties();
    }

    public static synchronized DatabaseConfig getInstance() {
        if (instance == null) {
            instance = new DatabaseConfig();
        }
        return instance;
    }

    private Properties loadProperties() {
        Properties props = new Properties();
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(PROPERTIES_FILE)) {
            if (is != null) {
                props.load(is);
                logger.info("Application properties loaded successfully");
            } else {
                logger.warn("Could not find {}, using defaults", PROPERTIES_FILE);
            }
        } catch (IOException e) {
            logger.error("Error loading application properties", e);
        }
        return props;
    }

    public DataSource getDataSource() {
        if (dataSource == null || dataSource.isClosed()) {
            dataSource = createDataSource();
        }
        return dataSource;
    }

    private HikariDataSource createDataSource() {
        String dbType = properties.getProperty("db.type", "POSTGRESQL").toUpperCase();
        HikariConfig config = new HikariConfig();

        if ("MYSQL".equals(dbType)) {
            configureMySql(config);
        } else {
            configurePostgreSQL(config);
        }

        configurePool(config);

        logger.info("Creating {} connection pool", dbType);
        return new HikariDataSource(config);
    }

    private void configurePostgreSQL(HikariConfig config) {
        String host = properties.getProperty("db.postgresql.host", "localhost");
        String port = properties.getProperty("db.postgresql.port", "5432");
        String dbName = properties.getProperty("db.postgresql.name", "digischool_emis");
        String username = properties.getProperty("db.postgresql.username", "postgres");
        String password = properties.getProperty("db.postgresql.password", "");

        config.setJdbcUrl(String.format("jdbc:postgresql://%s:%s/%s", host, port, dbName));
        config.setUsername(username);
        config.setPassword(password);
        config.setDriverClassName("org.postgresql.Driver");
        config.addDataSourceProperty("currentSchema",
                properties.getProperty("db.postgresql.schema", "public"));
        config.addDataSourceProperty("stringtype", "unspecified");
    }

    private void configureMySql(HikariConfig config) {
        String host = properties.getProperty("db.mysql.host", "localhost");
        String port = properties.getProperty("db.mysql.port", "3306");
        String dbName = properties.getProperty("db.mysql.name", "digischool_emis");
        String username = properties.getProperty("db.mysql.username", "root");
        String password = properties.getProperty("db.mysql.password", "");

        config.setJdbcUrl(String.format(
                "jdbc:mysql://%s:%s/%s?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Africa/Nairobi",
                host, port, dbName));
        config.setUsername(username);
        config.setPassword(password);
        config.setDriverClassName("com.mysql.cj.jdbc.Driver");
    }

    private void configurePool(HikariConfig config) {
        config.setMinimumIdle(Integer.parseInt(
                properties.getProperty("db.pool.minimum.idle", "5")));
        config.setMaximumPoolSize(Integer.parseInt(
                properties.getProperty("db.pool.maximum.size", "20")));
        config.setConnectionTimeout(Long.parseLong(
                properties.getProperty("db.pool.connection.timeout", "30000")));
        config.setIdleTimeout(Long.parseLong(
                properties.getProperty("db.pool.idle.timeout", "600000")));
        config.setMaxLifetime(Long.parseLong(
                properties.getProperty("db.pool.max.lifetime", "1800000")));
        config.setPoolName("DigiSchoolPool");
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    public String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    public void shutdown() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            logger.info("Database connection pool shut down");
        }
    }
}
