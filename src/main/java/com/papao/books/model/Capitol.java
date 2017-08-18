package com.papao.books.model;

import java.io.Serializable;

public class Capitol implements Serializable {

    private String nr;
    private String titlu;
    private String motto;
    private String pagina;

    public Capitol() {
    }

    public Capitol(String nr, String titlu, String pagina, String motto) {
        this.nr = nr;
        this.titlu = titlu;
        this.pagina = pagina;
        this.motto = motto;
    }

    public String getNr() {
        if (nr == null) {
            return "";
        }
        return nr;
    }

    public String getTitlu() {
        if (titlu == null) {
            return "";
        }
        return titlu;
    }

    public String getMotto() {
        if (motto == null) {
            return "";
        }
        return motto;
    }

    public String getPagina() {
        if (pagina == null) {
            return "";
        }
        return pagina;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Capitol capitol = (Capitol) o;

        return titlu.equals(capitol.titlu);
    }

    @Override
    public int hashCode() {
        return titlu.hashCode();
    }
}
