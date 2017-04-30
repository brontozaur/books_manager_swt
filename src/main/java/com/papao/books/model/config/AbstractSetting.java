package com.papao.books.model.config;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "config")
public abstract class AbstractSetting implements ISetting {

    @Id
    private ObjectId id;
    
    private ObjectId idUser;
    private String numeUser;
    private SettingType type;

    public AbstractSetting(ObjectId idUser, String numeUser, SettingType type) {
        this.idUser = idUser;
        this.numeUser = numeUser;
        this.type = type;
    }

    @Override
    public ObjectId getIdUser() {
        return idUser;
    }

    @Override
    public String getNumeUser() {
        return numeUser;
    }

    @Override
    public SettingType getType() {
        return type;
    }
}
