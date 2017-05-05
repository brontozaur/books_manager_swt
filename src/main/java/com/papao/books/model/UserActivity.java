package com.papao.books.model;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "userActivity")
public class UserActivity extends AuditObject {

    @Id
    private ObjectId id;

    private ObjectId userId;
    private ObjectId bookId;
    private List<Citat> citate = new ArrayList<>();
    private List<CarteCitita> cartiCitite = new ArrayList<>();
    private BookRating bookRating = new BookRating();
    private BookTranslationRating translationRating = new BookTranslationRating();

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

    public BookRating getBookRating() {
        return bookRating;
    }

    public void setBookRating(BookRating bookRating) {
        this.bookRating = bookRating;
    }

    public BookTranslationRating getTranslationRating() {
        return translationRating;
    }

    public void setTranslationRating(BookTranslationRating translationRating) {
        this.translationRating = translationRating;
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

    public int getRatingForBook(ObjectId bookId) {
        if (bookRating != null) {
            return bookRating.getRating();
        }
        return 0;
    }
}
