package com.papao.books.ui.searcheable;

public enum BookSearchType {

    Editura("Editură"),
    Autor("Autor"),
    An_aparitie("An apariție"),
    Limba("Limbă"),
    Titlu("Titlu"),
    Traducator("Traducător"),
    Tip_coperta("Tip copertă"),
    Limba_originala("Limba originală"),
    Creata("Creată"),
    Actualizata("Actualizată"),
    Nota_carte("Notă carte"),
    Nota_traducere("Notă traducere"),
    Locatie("Locație"),
    Cititori("Cititori"),
    Utilizatori("Utilizatori"),
    Gen_literar("Gen literar"),
    Taguri("Taguri"),
    Serie("Serie"),
    Colectie("Colecție"),
    Data_cumpararii("Data cumpărării"),
    Pret("Preț"),
    Lipsa_informatii("Lipsă informații");

    private String nume;

    BookSearchType(String nume) {
        this.nume = nume;
    }

    public String getNume() {
        return nume;
    }

    public static BookSearchType getByNume(String nume) {
        for (BookSearchType bookSearchType : BookSearchType.values()) {
            if (bookSearchType.getNume().equals(nume)) {
                return bookSearchType;
            }
        }
        return null;
    }
}
