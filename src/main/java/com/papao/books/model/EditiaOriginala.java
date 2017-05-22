package com.papao.books.model;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EditiaOriginala implements Serializable {

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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (StringUtils.isNotEmpty(titlu)) {
            sb.append("titlu: ").append(titlu);
        }
        sb.append(";");
        if (limba != null) {
            sb.append("limba: ").append(limba);
        }
        sb.append(";");
        if (StringUtils.isNotEmpty(editura)) {
            sb.append("editura: ").append(editura);
        }
        sb.append(";");
        if (ilustratori != null) {
            sb.append("ilustratori: ").append(Arrays.toString(ilustratori.toArray()));
        }
        sb.append(";");
        if (StringUtils.isNotEmpty(tara)) {
            sb.append("editura: ").append(tara);
        }
        sb.append(";");
        if (StringUtils.isNotEmpty(an)) {
            sb.append("an: ").append(an);
        }
        sb.append(";");
        return sb.toString();
    }
}
