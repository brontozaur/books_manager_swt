package com.papao.books.model;

import org.bson.types.ObjectId;

public class GridFsImageData {

    private ObjectId id;
    private String fileName;

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
