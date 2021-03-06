package com.papao.books.model;

import java.io.Serializable;

public class Personaj implements Serializable {

    private String nume;
    private String rol;
    private String descriere;

    public String getNume() {
        if (nume == null) {
            return "";
        }
        return nume;
    }

    public void setNume(String nume) {
        this.nume = nume;
    }

    public String getRol() {
        if (rol == null) {
            return "";
        }
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public String getDescriere() {
        if (descriere == null) {
            return "";
        }
        return descriere;
    }

    public void setDescriere(String descriere) {
        this.descriere = descriere;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Personaj)) {
            return false;
        }
        return ((Personaj) obj).getNume().equals(this.nume);
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
