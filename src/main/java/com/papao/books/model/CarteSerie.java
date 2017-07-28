package com.papao.books.model;

public class CarteSerie {

    private String nume;
    private String volum;

    public CarteSerie(String nume, String volum) {
        this.nume = nume;
        this.volum = volum;
    }

    public String getNume() {
        return nume;
    }

    public String getVolum() {
        return volum;
    }

    public String getFormattedValue() {
        return this.nume + " #" + this.volum;
    }
}
