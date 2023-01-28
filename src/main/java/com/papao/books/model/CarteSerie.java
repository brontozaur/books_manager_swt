package com.papao.books.model;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

public class CarteSerie implements Serializable {

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
        if (StringUtils.isNotEmpty(this.nume) && StringUtils.isNotEmpty(this.volum)) {
            return this.nume + " #" + this.volum;
        } else if (StringUtils.isEmpty(this.nume)) {
            return this.volum;
        } else return this.nume;
    }
}
