package com.papao.books.model;

import com.papao.books.view.util.NumberUtil;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Transient;

import java.io.Serializable;
import java.util.Date;

public class DocumentData implements Serializable {

    private ObjectId id = new ObjectId();
    private String fileName = "";

    @Transient
    private String filePath = "";

    @Transient
    private String contentType = "";

    @Transient
    private long length;

    @Transient
    private Date uploadDate;

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

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public long getLength() {
        return length;
    }

    public void setLength(long length) {
        this.length = length;
    }

    public Date getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(Date uploadDate) {
        this.uploadDate = uploadDate;
    }

    public String getSizeInKb() {
        return NumberUtil.formatNumber(length / 1024d, 2);
    }

    public String getSizeInMb() {
        return NumberUtil.formatNumber(length / 1024d / 1024d, 2);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DocumentData that = (DocumentData) o;

        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
