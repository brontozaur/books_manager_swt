package com.papao.books.model;

public class AnLunaZiData {

    private int an;
    private int luna;
    private int zi;
    private boolean showLabels;

    public AnLunaZiData(int an, int luna, int zi, boolean showLabels) {
        this.an = an;
        this.luna = luna;
        this.zi = zi;
        this.showLabels = showLabels;
    }

    public int getAn() {
        return an;
    }

    public void setAn(int an) {
        this.an = an;
    }

    public int getLuna() {
        return luna;
    }

    public void setLuna(int luna) {
        this.luna = luna;
    }

    public int getZi() {
        return zi;
    }

    public void setZi(int zi) {
        this.zi = zi;
    }

    public boolean isShowLabels() {
        return showLabels;
    }

    public void setShowLabels(boolean showLabels) {
        this.showLabels = showLabels;
    }
}
