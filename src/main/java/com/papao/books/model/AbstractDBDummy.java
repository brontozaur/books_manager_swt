package com.papao.books.model;

import java.util.UUID;

public class AbstractDBDummy extends AbstractDB {

	private final long id;

	public AbstractDBDummy() {
		this(UUID.randomUUID().getMostSignificantBits());
	}

	public AbstractDBDummy(long id) {
		this.id = id;
	}

	@Override
	public AbstractDB cloneObject() {
		return null;
	}

	@Override
	public long getId() {
		return this.id;
	}

}
