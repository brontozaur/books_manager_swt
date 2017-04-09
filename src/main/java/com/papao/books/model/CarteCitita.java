package com.papao.books.model;

import org.springframework.data.annotation.Transient;

import java.util.Date;

public class CarteCitita {

    @Transient
    Date now = new Date();

    private Date dataStart = now;
    private Date dataStop = now;
    private boolean citita;

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

    public boolean isCitita() {
        return dataStart != null && dataStop != null && dataStart != dataStop;
    }
}
