package com.papao.books.model;

public enum NotaCarte {
    NOTA_NEACORDAT("neacordat"),
    NOTA1("1"),
    NOTA2("2"),
    NOTA3("3"),
    NOTA4("4"),
    NOTA5("5"),
    NOTA6("6"),
    NOTA7("7"),
    NOTA8("8"),
    NOTA9("9"),
    NOTA10("10"),
    NOTA_CAPODOPERA("capodopera");

    private String nota;

    NotaCarte(String nota) {
        this.nota = nota;
    }

    public String getNota() {
        return nota;
    }
}
