package com.papao.books.model.config;

import org.bson.types.ObjectId;

public interface ISetting {

    SettingType getType();

    ObjectId getIdUser();

    String getNumeUser();

    boolean isValid();
}
