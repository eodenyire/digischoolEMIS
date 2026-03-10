package com.digischool.emis.ui.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

/**
 * Controller for the Main Dashboard screen.
 * Manages navigation between all modules and displays statistics.
 */
public class MainDashboardController implements Initializable {

    private static final Logger logger = LoggerFactory.getLogger(MainDashboardController.class);

    @FXML private Label schoolNameLabel;
    @FXML private Label currentUserLabel;
    @FXML private Label currentDateLabel;
    @FXML private StackPane contentArea;
    @FXML private VBox welcomePane;
    @FXML private Label statusLabel;

    // Dashboard stats labels
    @FXML private Label totalStudentsLabel;
    @FXML private Label totalTeachersLabel;
    @FXML private Label totalClassesLabel;
    @FXML private Label feeCollectionLabel;
    @FXML private Label attendanceRateLabel;
    @FXML private Label pendingFeesLabel;
    @FXML private Label booksIssuedLabel;
    @FXML private Label announcementsLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        updateDateTime();
        loadDashboardStats();
    }

    private void updateDateTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy | HH:mm");
        currentDateLabel.setText(LocalDateTime.now().format(formatter));
    }

    private void loadDashboardStats() {
        // In production, these would be loaded from the database
        // For demo, showing placeholder values
        totalStudentsLabel.setText("0");
        totalTeachersLabel.setText("0");
        totalClassesLabel.setText("0");
        feeCollectionLabel.setText("KES 0");
        attendanceRateLabel.setText("0%");
        pendingFeesLabel.setText("0");
        booksIssuedLabel.setText("0");
        announcementsLabel.setText("0");

        setStatus("Dashboard loaded successfully");
    }

    // ============================================================
    // Navigation Handlers
    // ============================================================

    @FXML private void showDashboard() {
        welcomePane.setVisible(true);
        setStatus("Dashboard");
        logger.debug("Showing dashboard");
    }

    @FXML private void showStudents() {
        setStatus("Students - Student Information System");
        logger.debug("Showing students module");
    }

    @FXML private void showTeachers() {
        setStatus("Teachers - Teacher Management");
        logger.debug("Showing teachers module");
    }

    @FXML private void showClasses() {
        setStatus("Classes - Academic Management");
        logger.debug("Showing classes module");
    }

    @FXML private void showSubjects() {
        setStatus("Subjects - Academic Management");
        logger.debug("Showing subjects module");
    }

    @FXML private void showTimetable() {
        setStatus("Timetable & Scheduling");
        logger.debug("Showing timetable module");
    }

    @FXML private void showAssessments() {
        setStatus("Assessments & Grading");
        logger.debug("Showing assessments module");
    }

    @FXML private void showGrades() {
        setStatus("Grades - Assessment & Grading");
        logger.debug("Showing grades module");
    }

    @FXML private void showReportCards() {
        setStatus("Report Cards");
        logger.debug("Showing report cards module");
    }

    @FXML private void showCbcCompetencies() {
        setStatus("CBC Competency Tracking Engine");
        logger.debug("Showing CBC competencies module");
    }

    @FXML private void showAttendance() {
        setStatus("Attendance Management");
        logger.debug("Showing attendance module");
    }

    @FXML private void showFeeManagement() {
        setStatus("Finance & Fee Management");
        logger.debug("Showing fee management module");
    }

    @FXML private void showPayments() {
        setStatus("Fee Payments");
        logger.debug("Showing payments module");
    }

    @FXML private void showLms() {
        setStatus("Learning Management System (LMS)");
        logger.debug("Showing LMS module");
    }

    @FXML private void showLibrary() {
        setStatus("Library Management");
        logger.debug("Showing library module");
    }

    @FXML private void showTransport() {
        setStatus("Transport Management");
        logger.debug("Showing transport module");
    }

    @FXML private void showBoarding() {
        setStatus("Boarding/Hostel Management");
        logger.debug("Showing boarding module");
    }

    @FXML private void showHealthRecords() {
        setStatus("Health & Medical Records");
        logger.debug("Showing health records module");
    }

    @FXML private void showDiscipline() {
        setStatus("Discipline & Behavior Management");
        logger.debug("Showing discipline module");
    }

    @FXML private void showAnnouncements() {
        setStatus("Communication - Announcements");
        logger.debug("Showing announcements module");
    }

    @FXML private void showMessages() {
        setStatus("Communication - Messages");
        logger.debug("Showing messages module");
    }

    @FXML private void showAnalytics() {
        setStatus("Analytics & AI Predictions");
        logger.debug("Showing analytics module");
    }

    @FXML private void showDocuments() {
        setStatus("Document & Certificate Management");
        logger.debug("Showing documents module");
    }

    @FXML private void showAlumni() {
        setStatus("Alumni Management");
        logger.debug("Showing alumni module");
    }

    @FXML private void showSettings() {
        setStatus("System Settings");
        logger.debug("Showing settings module");
    }

    @FXML private void showAdmin() {
        setStatus("System Administration & Security");
        logger.debug("Showing admin module");
    }

    @FXML
    private void handleLogout() {
        logger.info("User logging out");
        com.digischool.emis.ui.MainApplication.showLoginScreen();
    }

    private void setStatus(String message) {
        statusLabel.setText(message);
    }
}
