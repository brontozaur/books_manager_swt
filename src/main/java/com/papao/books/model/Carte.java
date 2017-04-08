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

    private List<Autor> autori = new ArrayList<>();
    private String titlu = "";

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Autor> getAutori() {
        return autori;
    }

    public void setAutori(List<Autor> autori) {
        if (autori == null || autori.isEmpty()) {
            autori = new ArrayList<>();
        }
        Collections.sort(autori, new Comparator<Autor>() {
            @Override
            public int compare(Autor o1, Autor o2) {
                return o1.getNume().compareTo(o2.getNume());
            }
        });
        this.autori = autori;
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

    public String getNumeAutori() {
        StringBuilder numeAutori = new StringBuilder();
        for (Autor autor : autori) {
            if (numeAutori.length() > 0) {
                numeAutori.append(",");
            }
            numeAutori.append(autor.getNume());
        }
        return numeAutori.toString();
    }
}
