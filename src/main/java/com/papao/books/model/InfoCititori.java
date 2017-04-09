package com.papao.books.model;

import java.util.ArrayList;
import java.util.List;

public class InfoCititori {

    private String numeCititor;
    private List<String> noteDeLectura = new ArrayList<>();
    private List<String> review = new ArrayList<>();
    private List<CarteCitita> citita = new ArrayList<>();
    private NotaCarte nota = NotaCarte.NOTA_NEACORDAT;
    private GenCarte genCarte = GenCarte.NESPECIFICAT;
    private List<String> taguri = new ArrayList<>();

    public String getNumeCititor() {
        return numeCititor;
    }

    public void setNumeCititor(String numeCititor) {
        this.numeCititor = numeCititor;
    }

    public List<String> getNoteDeLectura() {
        return noteDeLectura;
    }

    public void setNoteDeLectura(List<String> noteDeLectura) {
        this.noteDeLectura = noteDeLectura;
    }

    public List<String> getReview() {
        return review;
    }

    public void setReview(List<String> review) {
        this.review = review;
    }

    public List<CarteCitita> getCitita() {
        return citita;
    }

    public void setCitita(List<CarteCitita> citita) {
        this.citita = citita;
    }

    public NotaCarte getNota() {
        return nota;
    }

    public void setNota(NotaCarte nota) {
        this.nota = nota;
    }

    public GenCarte getGenCarte() {
        return genCarte;
    }

    public void setGenCarte(GenCarte genCarte) {
        this.genCarte = genCarte;
    }

    public List<String> getTaguri() {
        return taguri;
    }

    public void setTaguri(List<String> taguri) {
        this.taguri = taguri;
    }
}
