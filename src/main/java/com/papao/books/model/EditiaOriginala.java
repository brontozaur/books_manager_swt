package com.papao.books.model;

import java.util.ArrayList;
import java.util.List;

public class EditiaOriginala {

    private String titlu;
    private Limba limba;
    private String editura;
    private List<String> ilustratori = new ArrayList<>();
    private String tara;
    private String an;

    public String getTitlu() {
        if (this.titlu == null) {
            return "";
        }
        return titlu;
    }

    public void setTitlu(String titlu) {
        this.titlu = titlu;
    }

    public Limba getLimba() {
        if (this.limba == null) {
            return Limba.Nespecificat;
        }
        return limba;
    }

    public void setLimba(Limba limba) {
        this.limba = limba;
    }

    public String getEditura() {
        if (this.editura == null) {
            return "";
        }
        return editura;
    }

    public void setEditura(String editura) {
        this.editura = editura;
    }

    public List<String> getIlustratori() {
        return ilustratori;
    }

    public void setIlustratori(List<String> ilustratori) {
        this.ilustratori = ilustratori;
    }

    public String getTara() {
        if (this.tara == null) {
            return "";
        }
        return tara;
    }

    public void setTara(String tara) {
        this.tara = tara;
    }

    public String getAn() {
        if (this.an == null) {
            return "";
        }
        return an;
    }

    public void setAn(String an) {
        this.an = an;
    }
}
