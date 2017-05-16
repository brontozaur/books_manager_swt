package com.papao.books.model;

import java.util.Date;

public class CarteCitita {

    private boolean citita;
    private Date dataStart;
    private Date dataStop;

    public void setCitita(boolean citita) {
        this.citita = citita;
    }

    public boolean isCitita() {
        return citita;
    }

    public Date getDataStart() {
        return dataStart;
    }

    public void setDataStart(Date dataStart) {
        this.dataStart = dataStart;
    }

    public Date getDataStop() {
        return dataStop;
    }

    public void setDataStop(Date dataStop) {
        this.dataStop = dataStop;
    }

}
