package com.papao.books.view.interfaces;

import org.eclipse.swt.widgets.Listener;

public interface IConfig extends Listener {

    String FILTRARE_DEFAULT = "<filtrare>";

    void createContents();

    void populateFields();

    void save();

    boolean validate();

    void disable();

    String getCatName();

}
