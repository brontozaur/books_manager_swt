package com.papao.books.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "editura")
public class Editura extends AbstractDB{

    @Id
    private String id;
    private String nume = "";

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNume() {
        if (nume == null) {
            nume = "#";
        }
        return nume;
    }

    public void setNume(String nume) {
        this.nume = nume;
    }

}
