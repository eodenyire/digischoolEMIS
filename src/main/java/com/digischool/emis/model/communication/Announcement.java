package com.digischool.emis.model.communication;

import com.digischool.emis.model.BaseEntity;
import java.time.LocalDateTime;

/**
 * Represents an announcement sent to school community.
 */
public class Announcement extends BaseEntity {

    private Long schoolId;
    private String title;
    private String content;
    private String announcementType;
    private String targetAudience; // ALL, TEACHERS, STUDENTS, PARENTS
    private String targetClassIds; // JSON array
    private String attachmentUrl;
    private boolean isPublished;
    private LocalDateTime publishDate;
    private LocalDateTime expireDate;
    private Long createdBy;

    public Announcement() {
        this.announcementType = "GENERAL";
        this.targetAudience = "ALL";
        this.isPublished = false;
    }

    // Getters and Setters
    public Long getSchoolId() { return schoolId; }
    public void setSchoolId(Long schoolId) { this.schoolId = schoolId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getAnnouncementType() { return announcementType; }
    public void setAnnouncementType(String announcementType) {
        this.announcementType = announcementType;
    }

    public String getTargetAudience() { return targetAudience; }
    public void setTargetAudience(String targetAudience) {
        this.targetAudience = targetAudience;
    }

    public String getTargetClassIds() { return targetClassIds; }
    public void setTargetClassIds(String targetClassIds) {
        this.targetClassIds = targetClassIds;
    }

    public String getAttachmentUrl() { return attachmentUrl; }
    public void setAttachmentUrl(String attachmentUrl) { this.attachmentUrl = attachmentUrl; }

    public boolean isPublished() { return isPublished; }
    public void setPublished(boolean published) { isPublished = published; }

    public LocalDateTime getPublishDate() { return publishDate; }
    public void setPublishDate(LocalDateTime publishDate) { this.publishDate = publishDate; }

    public LocalDateTime getExpireDate() { return expireDate; }
    public void setExpireDate(LocalDateTime expireDate) { this.expireDate = expireDate; }

    public Long getCreatedBy() { return createdBy; }
    public void setCreatedBy(Long createdBy) { this.createdBy = createdBy; }
}
