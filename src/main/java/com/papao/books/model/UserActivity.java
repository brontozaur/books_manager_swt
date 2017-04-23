package com.papao.books.model;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "userActivity")
public class UserActivity {

    @Id
    private ObjectId id;

    private ObjectId userId;
    private List<Citat> citate = new ArrayList<>();
    private List<CarteCitita> cartiCitite = new ArrayList<>();
    private List<BookRating> bookRatings = new ArrayList<>();
    private List<BookTranslationRating> translationRatings;

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

    public List<Citat> getCitate() {
        return citate;
    }

    public void setCitate(List<Citat> citate) {
        this.citate = citate;
    }

    public List<CarteCitita> getCartiCitite() {
        return cartiCitite;
    }

    public void setCartiCitite(List<CarteCitita> cartiCitite) {
        this.cartiCitite = cartiCitite;
    }

    public List<BookRating> getBookRatings() {
        return bookRatings;
    }

    public void setBookRatings(List<BookRating> bookRatings) {
        this.bookRatings = bookRatings;
    }

    public List<BookTranslationRating> getTranslationRatings() {
        return translationRatings;
    }

    public void setTranslationRatings(List<BookTranslationRating> translationRatings) {
        this.translationRatings = translationRatings;
    }

    public int getRatingForBook(ObjectId bookId) {
        int bookRating = 0;
        for (BookRating rating : getBookRatings()) {
            if (rating.getBookId().equals(bookId)) {
                bookRating += rating.getRating();
            }
        }
        return bookRating;
    }
}
