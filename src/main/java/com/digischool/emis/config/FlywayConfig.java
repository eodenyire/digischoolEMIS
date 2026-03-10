package com.digischool.emis.config;

import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;

/**
 * Manages database migrations using Flyway.
 * Automatically applies SQL migration scripts on startup.
 */
public class FlywayConfig {

    private static final Logger logger = LoggerFactory.getLogger(FlywayConfig.class);

    private final DataSource dataSource;
    private final DatabaseConfig dbConfig;

    public FlywayConfig(DataSource dataSource, DatabaseConfig dbConfig) {
        this.dataSource = dataSource;
        this.dbConfig = dbConfig;
    }

    /**
     * Runs database migrations. Creates schema if it doesn't exist.
     */
    public void migrate() {
        boolean enabled = Boolean.parseBoolean(
                dbConfig.getProperty("flyway.enabled", "true"));

        if (!enabled) {
            logger.info("Flyway migrations disabled");
            return;
        }

        logger.info("Running Flyway database migrations...");

        try {
            Flyway flyway = Flyway.configure()
                    .dataSource(dataSource)
                    .locations(dbConfig.getProperty("flyway.locations", "classpath:db/migration"))
                    .baselineOnMigrate(Boolean.parseBoolean(
                            dbConfig.getProperty("flyway.baseline.on.migrate", "true")))
                    .validateOnMigrate(Boolean.parseBoolean(
                            dbConfig.getProperty("flyway.validate.on.migrate", "true")))
                    .outOfOrder(false)
                    .load();

            int applied = flyway.migrate().migrationsExecuted;
            logger.info("Flyway migrations completed: {} scripts applied", applied);

        } catch (Exception e) {
            logger.error("Failed to run Flyway migrations", e);
            throw new RuntimeException("Database migration failed", e);
        }
    }

    /**
     * Validates that migrations are applied correctly.
     */
    public boolean validate() {
        try {
            Flyway flyway = Flyway.configure()
                    .dataSource(dataSource)
                    .locations(dbConfig.getProperty("flyway.locations", "classpath:db/migration"))
                    .load();
            flyway.validate();
            return true;
        } catch (Exception e) {
            logger.error("Flyway validation failed", e);
            return false;
        }
    }
}
