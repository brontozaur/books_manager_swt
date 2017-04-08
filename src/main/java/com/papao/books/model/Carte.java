package com.papao.books.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Document(collection = "carte")
public class Carte extends AbstractDB {

    @Id
    private String id;

    private List<String> autori = new ArrayList<>();
    private String titlu = "";

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitlu() {
        if (titlu == null) {
            return "#";
        }
        return titlu;
    }

    public void setTitlu(String titlu) {
        this.titlu = titlu;
    }

    public List<String> getAutori() {
        return autori;
    }

    public void setAutori(List<String> autori) {
        this.autori = autori;
    }

    public String getNumeAutori(List<Autor> autori) {
        StringBuilder numeAutori = new StringBuilder();
        for (Autor autor : autori) {
            if (numeAutori.length() > 0) {
                numeAutori.append(", ");
            }
            numeAutori.append(autor.getNume());
        }
        return numeAutori.toString();
    }
}
