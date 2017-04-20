package com.papao.books.model;

import java.io.Serializable;

public class PremiuLiterar implements Serializable {

    private String an;
    private String premiu;

    public String getAn() {
        if (this.an == null) {
            return "";
        }
        return an;
    }

    public void setAn(String an) {
        this.an = an;
    }

    public String getPremiu() {
        if (premiu == null) {
            return "";
        }
        return premiu;
    }

    public void setPremiu(String premiu) {
        this.premiu = premiu;
    }
}
