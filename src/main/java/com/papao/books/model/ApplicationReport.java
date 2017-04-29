package com.papao.books.model;

import com.papao.books.export.ExportType;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

@Document(collection = "reports")
public class ApplicationReport extends AbstractMongoDB {

    private static String SESSION_ID = UUID.randomUUID().toString();

    @Id
    private ObjectId id;

    private ObjectId idUser;
    private ExportType type;
    private String cale;
    private String sesiune = SESSION_ID;
    private String nume;

    @CreatedBy
    private String createdBy;

    @CreatedDate
    private String createdAt;

    public ExportType getType() {
        if (type == null) {
            return ExportType.PDF;
        }
        return type;
    }

    public void setType(ExportType type) {
        this.type = type;
    }

    public String getCale() {
        return this.cale;
    }

    public void setCale(final String cale) {
        this.cale = cale;
    }

    public String getSesiune() {
        return this.sesiune;
    }

    public void setSesiune(final String sesiune) {
        this.sesiune = sesiune;
    }

    public String getNume() {
        return this.nume;
    }

    public void setNume(final String nume) {
        this.nume = nume;
    }

    @Override
    public ObjectId getId() {
        return this.id;
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

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
