package com.papao.books.model;

import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "userActivity")
@CompoundIndex(name = "uniqueUserActivityForBook", def = "{'bookId': 1, 'userId': 1}", unique = true)
public class UserActivity extends AuditObject {

    @Id
    private ObjectId id;

    private ObjectId userId;
    private ObjectId bookId;
    private List<Citat> citate = new ArrayList<>();
    private List<Notita> notite = new ArrayList<>();
    private CarteCitita carteCitita;
    private int rating;
    private int translationRating;
    private String review;

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public ObjectId getUserId() {
        return userId;
    }

    public void setUserId(ObjectId userId) {
        this.userId = userId;
    }

    public ObjectId getBookId() {
        return bookId;
    }

    public void setBookId(ObjectId bookId) {
        this.bookId = bookId;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public int getTranslationRating() {
        return translationRating;
    }

    public void setTranslationRating(int translationRating) {
        this.translationRating = translationRating;
    }

    public List<Citat> getCitate() {
        return citate;
    }

    public void setCitate(List<Citat> citate) {
        this.citate = citate;
    }

    public List<Notita> getNotite() {
        return notite;
    }

    public void setNotite(List<Notita> notite) {
        this.notite = notite;
    }

    public CarteCitita getCarteCitita() {
        return carteCitita;
    }

    public void setCarteCitita(CarteCitita carteCitita) {
        this.carteCitita = carteCitita;
    }

    public String getReview() {
        if (this.review == null) {
            return "";
        }
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public boolean isChanged() {
        return !citate.isEmpty() || !notite.isEmpty() || translationRating > 0 || rating > 0 || carteCitita != null || StringUtils.isNotEmpty(review);
    }
}
