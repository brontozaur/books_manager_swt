package com.papao.books.ui.searcheable;

public enum CategoriePret {

    SUB_10("sub 10 lei", 0, 10),
    INTRE_10_15("intre 10 si 15 lei", 10, 15),
    INTRE_15_25("intre 15 si 25 lei", 15, 25),
    INTRE_25_50("intre 25 si 50 lei", 25, 50),
    PESTE_50("peste 50", 50, Integer.MAX_VALUE);

    private String descriere;
    private double min;
    private double max;

    CategoriePret(String descriere, double min, double max) {
        this.descriere = descriere;
        this.min = min;
        this.max = max;
    }

    public String getDescriere() {
        return descriere;
    }

    public double getMin() {
        return min;
    }

    public double getMax() {
        return max;
    }
}
