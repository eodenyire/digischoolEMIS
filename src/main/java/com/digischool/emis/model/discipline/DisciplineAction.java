package com.digischool.emis.model.discipline;

import com.digischool.emis.model.BaseEntity;
import java.time.LocalDate;

public class DisciplineAction extends BaseEntity {

    private Long incidentId;
    private String actionType;
    private String actionDetails;
    private LocalDate actionDate;
    private Long issuedBy;

    public DisciplineAction() {}

    public Long getIncidentId() { return incidentId; }
    public void setIncidentId(Long incidentId) { this.incidentId = incidentId; }

    public String getActionType() { return actionType; }
    public void setActionType(String actionType) { this.actionType = actionType; }

    public String getActionDetails() { return actionDetails; }
    public void setActionDetails(String actionDetails) { this.actionDetails = actionDetails; }

    public LocalDate getActionDate() { return actionDate; }
    public void setActionDate(LocalDate actionDate) { this.actionDate = actionDate; }

    public Long getIssuedBy() { return issuedBy; }
    public void setIssuedBy(Long issuedBy) { this.issuedBy = issuedBy; }

    @Override
    public String toString() {
        return "DisciplineAction{id=" + id + ", incidentId=" + incidentId + ", actionType='" + actionType + "'}";
    }
}
