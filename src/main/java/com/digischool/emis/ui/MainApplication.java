package com.digischool.emis.ui;

import com.digischool.emis.config.DatabaseConfig;
import com.digischool.emis.config.FlywayConfig;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.IOException;

/**
 * Main JavaFX Application class for DigiSchool EMIS.
 * Handles application lifecycle and scene management.
 */
public class MainApplication extends Application {

    private static final Logger logger = LoggerFactory.getLogger(MainApplication.class);

    private static Stage primaryStage;
    private static DatabaseConfig dbConfig;
    private static DataSource dataSource;

    @Override
    public void init() throws Exception {
        logger.info("Initializing DigiSchool EMIS application...");

        // Initialize database
        dbConfig = DatabaseConfig.getInstance();

        try {
            dataSource = dbConfig.getDataSource();
            logger.info("Database connection established");

            // Run Flyway migrations
            FlywayConfig flyway = new FlywayConfig(dataSource, dbConfig);
            flyway.migrate();
            logger.info("Database migrations completed");

        } catch (Exception e) {
            logger.warn("Database not available, running in offline mode: {}", e.getMessage());
            // Continue without DB - show setup screen
        }
    }

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;

        // Configure primary stage
        stage.setTitle(dbConfig.getProperty("app.title",
                "DigiSchool EMIS - Kenya CBC School Management System"));
        stage.setMinWidth(1024);
        stage.setMinHeight(768);
        stage.setWidth(1366);
        stage.setHeight(768);

        // Load login screen
        showLoginScreen();

        // Handle close event
        stage.setOnCloseRequest(event -> {
            logger.info("Application closing...");
            dbConfig.shutdown();
            Platform.exit();
        });

        stage.show();
        logger.info("DigiSchool EMIS started successfully");
    }

    @Override
    public void stop() throws Exception {
        logger.info("Stopping DigiSchool EMIS...");
        if (dbConfig != null) {
            dbConfig.shutdown();
        }
    }

    /**
     * Shows the login screen.
     */
    public static void showLoginScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    MainApplication.class.getResource("/fxml/LoginView.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root, 800, 600);
            scene.getStylesheets().add(
                    MainApplication.class.getResource("/css/login.css").toExternalForm());

            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.centerOnScreen();

        } catch (IOException e) {
            logger.error("Failed to load login screen", e);
            showFallbackLoginScreen();
        }
    }

    /**
     * Shows the main dashboard after successful login.
     */
    public static void showMainDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    MainApplication.class.getResource("/fxml/MainDashboard.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            scene.getStylesheets().add(
                    MainApplication.class.getResource("/css/main.css").toExternalForm());

            primaryStage.setScene(scene);
            primaryStage.setResizable(true);
            primaryStage.setMaximized(true);
            primaryStage.centerOnScreen();

        } catch (IOException e) {
            logger.error("Failed to load main dashboard", e);
        }
    }

    private static void showFallbackLoginScreen() {
        // Fallback if FXML fails - show a basic scene
        javafx.scene.control.Label label = new javafx.scene.control.Label(
                "DigiSchool EMIS\nLoading...");
        label.setStyle("-fx-font-size: 24px; -fx-alignment: center;");
        Scene scene = new Scene(new javafx.scene.layout.StackPane(label), 800, 600);
        primaryStage.setScene(scene);
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static DataSource getDataSource() {
        return dataSource;
    }

    public static DatabaseConfig getDbConfig() {
        return dbConfig;
    }
}
