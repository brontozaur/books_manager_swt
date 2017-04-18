package com.papao.books.model;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Transient;

import java.util.Date;

public class CarteCitita {

    @Transient
    private Date now = new Date();

    private Date dataStart = now;
    private Date dataStop = now;
    private int notaCarte;
    private ObjectId idUser;

    private String createdBy;

    @CreatedDate
    private Date createdAt;

    private String updatedBy;

    @LastModifiedDate
    private Date updatedAt;

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

    public int getNotaCarte() {
        return notaCarte;
    }

    public void setNotaCarte(int notaCarte) {
        this.notaCarte = notaCarte;
    }

    public ObjectId getIdUser() {
        return idUser;
    }

    public void setIdUser(ObjectId idUser) {
        this.idUser = idUser;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}
