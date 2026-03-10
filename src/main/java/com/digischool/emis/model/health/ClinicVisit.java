package com.digischool.emis.model.health;

import com.digischool.emis.model.BaseEntity;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Records a student's visit to the school clinic.
 */
public class ClinicVisit extends BaseEntity {

    private Long studentId;
    private LocalDateTime visitDate;
    private String complaint;
    private String diagnosis;
    private String treatment;
    private String medicationPrescribed;
    private boolean isReferredToHospital;
    private String hospitalReferredTo;
    private LocalDate followUpDate;
    private String attendedBy;
    private String notes;

    public ClinicVisit() {
        this.isReferredToHospital = false;
    }

    // Getters and Setters
    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }

    public LocalDateTime getVisitDate() { return visitDate; }
    public void setVisitDate(LocalDateTime visitDate) { this.visitDate = visitDate; }

    public String getComplaint() { return complaint; }
    public void setComplaint(String complaint) { this.complaint = complaint; }

    public String getDiagnosis() { return diagnosis; }
    public void setDiagnosis(String diagnosis) { this.diagnosis = diagnosis; }

    public String getTreatment() { return treatment; }
    public void setTreatment(String treatment) { this.treatment = treatment; }

    public String getMedicationPrescribed() { return medicationPrescribed; }
    public void setMedicationPrescribed(String medicationPrescribed) {
        this.medicationPrescribed = medicationPrescribed;
    }

    public boolean isReferredToHospital() { return isReferredToHospital; }
    public void setReferredToHospital(boolean referredToHospital) {
        isReferredToHospital = referredToHospital;
    }

    public String getHospitalReferredTo() { return hospitalReferredTo; }
    public void setHospitalReferredTo(String hospitalReferredTo) {
        this.hospitalReferredTo = hospitalReferredTo;
    }

    public LocalDate getFollowUpDate() { return followUpDate; }
    public void setFollowUpDate(LocalDate followUpDate) { this.followUpDate = followUpDate; }

    public String getAttendedBy() { return attendedBy; }
    public void setAttendedBy(String attendedBy) { this.attendedBy = attendedBy; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
