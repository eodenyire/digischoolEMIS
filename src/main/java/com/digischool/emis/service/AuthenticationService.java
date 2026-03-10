package com.digischool.emis.service;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.digischool.emis.config.DatabaseConfig;
import com.digischool.emis.model.admin.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Authentication service handling user login, logout, and session management.
 * Uses BCrypt for password hashing.
 */
public class AuthenticationService {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);

    private static final int MAX_LOGIN_ATTEMPTS = 5;
    private static final int LOCKOUT_MINUTES = 30;

    private final DataSource dataSource;
    private final DatabaseConfig dbConfig;

    private User currentUser;

    public AuthenticationService(DataSource dataSource, DatabaseConfig dbConfig) {
        this.dataSource = dataSource;
        this.dbConfig = dbConfig;
    }

    /**
     * Authenticates a user with username/email and password.
     *
     * @param usernameOrEmail Username or email
     * @param password        Plain text password
     * @return Authenticated user if credentials are valid
     * @throws AuthenticationException if authentication fails
     */
    public User authenticate(String usernameOrEmail, String password) {
        if (usernameOrEmail == null || usernameOrEmail.trim().isEmpty()) {
            throw new AuthenticationException("Username or email is required");
        }
        if (password == null || password.isEmpty()) {
            throw new AuthenticationException("Password is required");
        }

        Optional<User> userOpt = findUserByUsernameOrEmail(usernameOrEmail.trim());

        if (userOpt.isEmpty()) {
            logger.warn("Login attempt with unknown user: {}", usernameOrEmail);
            throw new AuthenticationException("Invalid username or password");
        }

        User user = userOpt.get();

        if (!user.isActive()) {
            throw new AuthenticationException("Account is deactivated. Contact administrator.");
        }

        if (user.isLocked()) {
            throw new AuthenticationException(
                    "Account is locked due to too many failed attempts. Contact administrator.");
        }

        boolean passwordMatch = BCrypt.verifyer()
                .verify(password.toCharArray(), user.getPasswordHash())
                .verified;

        if (!passwordMatch) {
            handleFailedLogin(user);
            logger.warn("Failed login attempt for user: {}", usernameOrEmail);
            throw new AuthenticationException("Invalid username or password");
        }

        // Reset failed attempts on success
        resetFailedAttempts(user.getId());
        updateLastLogin(user.getId());

        user.setLastLogin(LocalDateTime.now());
        this.currentUser = user;

        logger.info("User logged in: {}", user.getUsername());
        return user;
    }

    /**
     * Hashes a plain text password using BCrypt.
     */
    public String hashPassword(String plainPassword) {
        return BCrypt.withDefaults().hashToString(12, plainPassword.toCharArray());
    }

    /**
     * Logs out the current user.
     */
    public void logout() {
        if (currentUser != null) {
            logger.info("User logged out: {}", currentUser.getUsername());
            currentUser = null;
        }
    }

    /**
     * Returns the currently authenticated user.
     */
    public Optional<User> getCurrentUser() {
        return Optional.ofNullable(currentUser);
    }

    /**
     * Checks if a user is currently authenticated.
     */
    public boolean isAuthenticated() {
        return currentUser != null;
    }

    /**
     * Changes a user's password.
     */
    public void changePassword(Long userId, String currentPassword, String newPassword) {
        Optional<User> userOpt = findUserById(userId);
        if (userOpt.isEmpty()) {
            throw new AuthenticationException("User not found");
        }

        User user = userOpt.get();
        boolean currentMatch = BCrypt.verifyer()
                .verify(currentPassword.toCharArray(), user.getPasswordHash())
                .verified;

        if (!currentMatch) {
            throw new AuthenticationException("Current password is incorrect");
        }

        validatePasswordStrength(newPassword);
        String newHash = hashPassword(newPassword);
        updatePasswordHash(userId, newHash);
        logger.info("Password changed for user id={}", userId);
    }

    /**
     * Validates password strength requirements.
     */
    public void validatePasswordStrength(String password) {
        int minLength = Integer.parseInt(
                dbConfig.getProperty("security.password.min.length", "8"));

        if (password == null || password.length() < minLength) {
            throw new IllegalArgumentException(
                    "Password must be at least " + minLength + " characters long");
        }

        boolean requireUppercase = Boolean.parseBoolean(
                dbConfig.getProperty("security.password.require.uppercase", "true"));
        boolean requireLowercase = Boolean.parseBoolean(
                dbConfig.getProperty("security.password.require.lowercase", "true"));
        boolean requireDigits = Boolean.parseBoolean(
                dbConfig.getProperty("security.password.require.digits", "true"));

        if (requireUppercase && !password.chars().anyMatch(Character::isUpperCase)) {
            throw new IllegalArgumentException("Password must contain at least one uppercase letter");
        }
        if (requireLowercase && !password.chars().anyMatch(Character::isLowerCase)) {
            throw new IllegalArgumentException("Password must contain at least one lowercase letter");
        }
        if (requireDigits && !password.chars().anyMatch(Character::isDigit)) {
            throw new IllegalArgumentException("Password must contain at least one digit");
        }
    }

    private Optional<User> findUserByUsernameOrEmail(String usernameOrEmail) {
        String sql = "SELECT * FROM users WHERE username = ? OR email = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, usernameOrEmail);
            stmt.setString(2, usernameOrEmail);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapUser(rs));
            }
            return Optional.empty();

        } catch (SQLException e) {
            logger.error("Error finding user by username/email", e);
            throw new RuntimeException("Database error during authentication", e);
        }
    }

    private Optional<User> findUserById(Long id) {
        String sql = "SELECT * FROM users WHERE id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapUser(rs));
            }
            return Optional.empty();

        } catch (SQLException e) {
            logger.error("Error finding user by id", e);
            throw new RuntimeException("Database error", e);
        }
    }

    private void handleFailedLogin(User user) {
        int attempts = user.getFailedLoginAttempts() + 1;
        boolean shouldLock = attempts >= MAX_LOGIN_ATTEMPTS;

        String sql = "UPDATE users SET failed_login_attempts = ?, is_locked = ? WHERE id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, attempts);
            stmt.setBoolean(2, shouldLock);
            stmt.setLong(3, user.getId());
            stmt.executeUpdate();

            if (shouldLock) {
                logger.warn("User account locked after {} failed attempts: {}", attempts,
                        user.getUsername());
            }

        } catch (SQLException e) {
            logger.error("Error updating failed login attempts", e);
        }
    }

    private void resetFailedAttempts(Long userId) {
        String sql = "UPDATE users SET failed_login_attempts = 0, is_locked = FALSE WHERE id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, userId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            logger.error("Error resetting failed login attempts", e);
        }
    }

    private void updateLastLogin(Long userId) {
        String sql = "UPDATE users SET last_login = NOW() WHERE id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, userId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            logger.error("Error updating last login", e);
        }
    }

    private void updatePasswordHash(Long userId, String passwordHash) {
        String sql = "UPDATE users SET password_hash = ?, password_changed_at = NOW() WHERE id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, passwordHash);
            stmt.setLong(2, userId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            logger.error("Error updating password hash", e);
            throw new RuntimeException("Failed to update password", e);
        }
    }

    private User mapUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getLong("id"));
        user.setUsername(rs.getString("username"));
        user.setEmail(rs.getString("email"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setFirstName(rs.getString("first_name"));
        user.setLastName(rs.getString("last_name"));
        user.setPhone(rs.getString("phone"));
        user.setActive(rs.getBoolean("is_active"));
        user.setLocked(rs.getBoolean("is_locked"));
        user.setFailedLoginAttempts(rs.getInt("failed_login_attempts"));

        Timestamp lastLogin = rs.getTimestamp("last_login");
        if (lastLogin != null) user.setLastLogin(lastLogin.toLocalDateTime());

        user.setMustChangePassword(rs.getBoolean("must_change_password"));
        user.setProfilePictureUrl(rs.getString("profile_picture_url"));
        return user;
    }

    /**
     * Exception thrown when authentication fails.
     */
    public static class AuthenticationException extends RuntimeException {
        public AuthenticationException(String message) {
            super(message);
        }
    }
}
