package com.digischool.emis.model.library;

import com.digischool.emis.model.BaseEntity;

/**
 * Represents a book in the school library catalog.
 */
public class LibraryBook extends BaseEntity {

    private Long schoolId;
    private String title;
    private String isbn;
    private String author;
    private String publisher;
    private Integer publicationYear;
    private String edition;
    private String category;
    private Long subjectId;
    private Long gradeLevelId;
    private int totalCopies;
    private int availableCopies;
    private String shelfLocation;
    private String coverImageUrl;
    private String description;
    private boolean isActive;

    public LibraryBook() {
        this.totalCopies = 1;
        this.availableCopies = 1;
        this.isActive = true;
    }

    // Getters and Setters
    public Long getSchoolId() { return schoolId; }
    public void setSchoolId(Long schoolId) { this.schoolId = schoolId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public String getPublisher() { return publisher; }
    public void setPublisher(String publisher) { this.publisher = publisher; }

    public Integer getPublicationYear() { return publicationYear; }
    public void setPublicationYear(Integer publicationYear) {
        this.publicationYear = publicationYear;
    }

    public String getEdition() { return edition; }
    public void setEdition(String edition) { this.edition = edition; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public Long getSubjectId() { return subjectId; }
    public void setSubjectId(Long subjectId) { this.subjectId = subjectId; }

    public Long getGradeLevelId() { return gradeLevelId; }
    public void setGradeLevelId(Long gradeLevelId) { this.gradeLevelId = gradeLevelId; }

    public int getTotalCopies() { return totalCopies; }
    public void setTotalCopies(int totalCopies) { this.totalCopies = totalCopies; }

    public int getAvailableCopies() { return availableCopies; }
    public void setAvailableCopies(int availableCopies) {
        this.availableCopies = availableCopies;
    }

    public boolean isAvailable() {
        return availableCopies > 0;
    }

    public String getShelfLocation() { return shelfLocation; }
    public void setShelfLocation(String shelfLocation) { this.shelfLocation = shelfLocation; }

    public String getCoverImageUrl() { return coverImageUrl; }
    public void setCoverImageUrl(String coverImageUrl) { this.coverImageUrl = coverImageUrl; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
}
