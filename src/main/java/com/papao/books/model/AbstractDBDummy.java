package com.papao.books.model;

import java.util.UUID;

public class AbstractDBDummy extends AbstractDB {

    private final String id;

    public AbstractDBDummy() {
        this(UUID.randomUUID().toString());
    }

    public AbstractDBDummy(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return this.id;
    }

}
