package com.papao.books.model;

public class BookTranslationRating {

    private String createdBy;
    private String createdAt;
    private BookTranslationRating rating;
    private String updatedBy;
    private String updatedAt;

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public BookTranslationRating getRating() {
        return rating;
    }

    public void setRating(BookTranslationRating rating) {
        this.rating = rating;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}
