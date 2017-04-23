package com.papao.books.model;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "user")
public class User extends AbstractMongoDB {

    @Id
    private ObjectId id;

    private String nume;
    private String prenume;

    private List<Citat> citate = new ArrayList<>();
    private List<CarteCitita> cartiCitite = new ArrayList<>();
    private List<BookRating> bookRatings;
    private List<BookTranslationRating> translationRatings;

    @Override
    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getNume() {
        return nume;
    }

    public void setNume(String nume) {
        this.nume = nume;
    }

    public String getPrenume() {
        return prenume;
    }

    public void setPrenume(String prenume) {
        this.prenume = prenume;
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
        for (BookRating rating: getBookRatings()) {
            if (rating.getBookId().equals(bookId)) {
                return rating.getRating();
            }
        }
        return 0;
    }
}
