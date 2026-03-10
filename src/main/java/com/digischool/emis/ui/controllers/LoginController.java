package com.digischool.emis.ui.controllers;

import com.digischool.emis.config.DatabaseConfig;
import com.digischool.emis.model.admin.User;
import com.digischool.emis.service.AuthenticationService;
import com.digischool.emis.ui.MainApplication;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller for the Login screen.
 * Handles user authentication and navigation to main dashboard.
 */
public class LoginController implements Initializable {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private CheckBox rememberMeCheckbox;
    @FXML private Label errorLabel;
    @FXML private Button loginButton;
    @FXML private StackPane loadingOverlay;

    private AuthenticationService authService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize auth service if database is available
        if (MainApplication.getDataSource() != null) {
            authService = new AuthenticationService(
                    MainApplication.getDataSource(),
                    DatabaseConfig.getInstance());
        }

        // Allow Enter key to trigger login
        passwordField.setOnAction(e -> handleLogin());
        usernameField.setOnAction(e -> passwordField.requestFocus());

        // Clear error when user types
        usernameField.textProperty().addListener((obs, old, nw) -> hideError());
        passwordField.textProperty().addListener((obs, old, nw) -> hideError());

        // Focus username field
        Platform.runLater(() -> usernameField.requestFocus());
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (username.isEmpty()) {
            showError("Please enter your username or email");
            usernameField.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            showError("Please enter your password");
            passwordField.requestFocus();
            return;
        }

        // Show loading and disable form
        setFormEnabled(false);
        loadingOverlay.setVisible(true);

        // Run authentication in background thread
        Task<User> loginTask = new Task<>() {
            @Override
            protected User call() throws Exception {
                if (authService == null) {
                    // Demo mode when no database is configured
                    Thread.sleep(500); // Simulate delay
                    if ("admin".equals(username) && "Admin@123".equals(password)) {
                        User demoUser = new User();
                        demoUser.setId(1L);
                        demoUser.setUsername("admin");
                        demoUser.setEmail("admin@digischool.ke");
                        demoUser.setFirstName("System");
                        demoUser.setLastName("Administrator");
                        return demoUser;
                    }
                    throw new AuthenticationService.AuthenticationException(
                            "Invalid credentials. (Demo: admin/Admin@123)");
                }
                return authService.authenticate(username, password);
            }
        };

        loginTask.setOnSucceeded(e -> {
            User user = loginTask.getValue();
            logger.info("Login successful for: {}", user.getUsername());
            loadingOverlay.setVisible(false);
            MainApplication.showMainDashboard();
        });

        loginTask.setOnFailed(e -> {
            loadingOverlay.setVisible(false);
            setFormEnabled(true);

            Throwable ex = loginTask.getException();
            String errorMsg = ex instanceof AuthenticationService.AuthenticationException
                    ? ex.getMessage()
                    : "Login failed. Please try again.";

            showError(errorMsg);
            passwordField.clear();
            passwordField.requestFocus();
        });

        Thread thread = new Thread(loginTask);
        thread.setDaemon(true);
        thread.start();
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
    }

    private void hideError() {
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);
    }

    private void setFormEnabled(boolean enabled) {
        usernameField.setDisable(!enabled);
        passwordField.setDisable(!enabled);
        loginButton.setDisable(!enabled);
        rememberMeCheckbox.setDisable(!enabled);
    }
}
