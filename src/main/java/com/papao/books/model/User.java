package com.papao.books.model;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "user")
@CompoundIndex(name = "uniqueNumeAndPrenume", def = "{'nume': 1, 'prenume': 1}", unique = true)
public class User extends AuditObject {

    @Id
    private ObjectId id;

    private String nume = "";
    private String prenume = "";

    @Override
    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getNume() {
        return nume;
    }

    public void setNume(String nume) {
        this.nume = nume;
    }

    public String getPrenume() {
        return prenume;
    }

    public void setPrenume(String prenume) {
        this.prenume = prenume;
    }

    public String getNumeComplet() {
        return this.nume + " " + this.prenume;
    }

}
