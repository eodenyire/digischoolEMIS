package com.digischool.emis;

import com.digischool.emis.config.DatabaseConfig;
import com.digischool.emis.config.FlywayConfig;
import com.digischool.emis.ui.MainApplication;
import javafx.application.Application;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * DigiSchool EMIS - World-Class School Management System for Kenya CBC
 *
 * Main application entry point. Initializes:
 * 1. Application configuration
 * 2. Database connection
 * 3. Database migrations (Flyway)
 * 4. JavaFX UI
 */
public class DigiSchoolApp {

    private static final Logger logger = LoggerFactory.getLogger(DigiSchoolApp.class);

    public static void main(String[] args) {
        logger.info("Starting DigiSchool EMIS...");
        logger.info("Kenya CBC School Management System - Grade 1 to Grade 13");

        try {
            // Initialize application
            initializeApplication();

            // Launch JavaFX UI
            Application.launch(MainApplication.class, args);

        } catch (Exception e) {
            logger.error("Failed to start DigiSchool EMIS", e);
            System.exit(1);
        }
    }

    private static void initializeApplication() {
        logger.info("Initializing application configuration...");

        // Load configuration
        DatabaseConfig dbConfig = DatabaseConfig.getInstance();
        logger.info("App: {}", dbConfig.getProperty("app.name"));
        logger.info("Version: {}", dbConfig.getProperty("app.version"));

        // Note: Database migration runs after UI starts to show progress
        // See MainApplication.init() for the migration logic
    }
}
