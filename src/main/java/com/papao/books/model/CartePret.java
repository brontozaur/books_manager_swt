package com.papao.books.model;

import java.util.Date;

public class CartePret {

    private Date dataCumpararii;
    private double pretIntreg;
    private double pret;
    private String magazin;

    public CartePret(Date dataCumpararii, double pretIntreg, double pret, String magazin) {
        this.dataCumpararii = dataCumpararii;
        this.pretIntreg = pretIntreg;
        this.pret = pret;
        this.magazin = magazin;
    }

    public Date getDataCumpararii() {
        return dataCumpararii;
    }

    public double getPretIntreg() {
        return pretIntreg;
    }

    public double getPret() {
        return pret;
    }

    public String getMagazin() {
        if (this.magazin == null) {
            return "";
        }
        return magazin;
    }
}
