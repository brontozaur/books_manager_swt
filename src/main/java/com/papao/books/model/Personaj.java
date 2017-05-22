package com.papao.books.model;

import com.papao.books.ui.custom.ComboElement;

public class Personaj implements ComboElement{

    private String nume;
    private String rol;
    private String descriere;

    public String getNume() {
        return nume;
    }

    public void setNume(String nume) {
        this.nume = nume;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public String getDescriere() {
        return descriere;
    }

    public void setDescriere(String descriere) {
        this.descriere = descriere;
    }

    @Override
    public String getText() {
        return this.nume;
    }

    @Override
    public String toString() {
        return "Personaj{" +
                "nume='" + nume + '\'' +
                ", rol='" + rol + '\'' +
                ", descriere='" + descriere + '\'' +
                '}';
    }
}
