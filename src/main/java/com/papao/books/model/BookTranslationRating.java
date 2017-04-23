package com.papao.books.model;

import org.bson.types.ObjectId;

public class BookTranslationRating {

    private ObjectId bookId;
    private String createdBy;
    private String createdAt;
    private int ratingTraducere;
    private String updatedBy;
    private String updatedAt;

    public ObjectId getBookId() {
        return bookId;
    }

    public void setBookId(ObjectId bookId) {
        this.bookId = bookId;
    }

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

    public int getRatingTraducere() {
        return ratingTraducere;
    }

    public void setRatingTraducere(int ratingTraducere) {
        this.ratingTraducere = ratingTraducere;
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
