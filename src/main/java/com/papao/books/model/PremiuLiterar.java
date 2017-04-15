package com.papao.books.model;

import java.io.Serializable;

public class PremiuLiterar implements Serializable {

    private String an;
    private String premiu;

    public String getAn() {
        return an;
    }

    public void setAn(String an) {
        this.an = an;
    }

    public String getPremiu() {
        return premiu;
    }

    public void setPremiu(String premiu) {
        this.premiu = premiu;
    }
}
