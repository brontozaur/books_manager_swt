package com.papao.books.model;

public abstract class AbstractDB implements Cloneable {

    public static final String EMPTY = "";

	public AbstractDB() {
		super();
	}

	public abstract AbstractDB cloneObject() throws CloneNotSupportedException;

	public abstract long getId();

}
