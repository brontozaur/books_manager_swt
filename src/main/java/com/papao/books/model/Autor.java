package com.papao.books.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "autor")
public class Autor extends AbstractDB{

    @Id
    private String id;

    private String nume = "";
    private Date dataNasterii;

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

    public Date getDataNasterii() {
        return dataNasterii;
    }

    public void setDataNasterii(Date dataNasterii) {
        this.dataNasterii = dataNasterii;
    }
}
