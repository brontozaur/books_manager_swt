package com.papao.books.model;

import org.bson.types.ObjectId;

import java.io.Serializable;

public class DocumentData implements Serializable{

    private ObjectId id = new ObjectId();
    private String fileName = "";

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
