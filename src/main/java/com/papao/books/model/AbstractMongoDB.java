package com.papao.books.model;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Persistable;

public abstract class AbstractMongoDB implements Persistable<ObjectId> {

    @Override
    public boolean isNew() {
        return getId() == null;
    }
}
