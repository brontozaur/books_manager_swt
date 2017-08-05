package com.papao.books.model;

public class Capitol {

    private String nr;
    private String titlu;
    private String motto;
    private String pagina;

    public Capitol(String nr, String titlu) {
        this.nr = nr;
        this.titlu = titlu;
    }

    public Capitol(String nr, String titlu, String motto, String pagina) {
        this.nr = nr;
        this.titlu = titlu;
        this.motto = motto;
        this.pagina = pagina;
    }

    public String getNr() {
        return nr;
    }

    public String getTitlu() {
        return titlu;
    }

    public String getMotto() {
        return motto;
    }

    public String getPagina() {
        return pagina;
    }
}
