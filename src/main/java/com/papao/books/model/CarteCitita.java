package com.papao.books.model;

import java.util.Date;

public class CarteCitita {

    private Date dataStart;
    private Date dataStop;

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
        return dataStart != null && dataStop != null && dataStop.getTime() > dataStart.getTime();
    }

}
