package com.papao.books.model;

import org.springframework.data.annotation.Transient;

import java.util.Date;

public class CarteCitita {

    @Transient
    private Date now = new Date();

    private Date dataStart = now;
    private Date dataStop = now;
    private String createdBy;
    private String createdAt;
    private int notaCarte;
    private String lastModifiedAt;
    private String lastModifiedBy;

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

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getLastModifiedAt() {
        return lastModifiedAt;
    }

    public void setLastModifiedAt(String lastModifiedAt) {
        this.lastModifiedAt = lastModifiedAt;
    }

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public int getNotaCarte() {
        return notaCarte;
    }

    public void setNotaCarte(int notaCarte) {
        this.notaCarte = notaCarte;
    }
}
