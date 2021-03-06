package com.papao.books.ui.interfaces;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;

public abstract class AbstractIConfigAdapter extends Composite implements IConfig {

	/**
	 * A simple adapter for the IConfig interface, extending Composite, for conveninence
	 **/
	protected AbstractIConfigAdapter(final Composite parent) {
		this(parent, SWT.NONE);
	}

	protected AbstractIConfigAdapter(final Composite parent, final int style) {
		super(parent, style);
	}

	@Override
	public void createContents() {
	}

	@Override
	public void populateFields() {
	}

	@Override
	public void disable() {
	}

	@Override
	public boolean validate() {
		return true;
	}

	@Override
	public void handleEvent(final Event event) {
	}

    @Override
    public void save() {
    }

}
