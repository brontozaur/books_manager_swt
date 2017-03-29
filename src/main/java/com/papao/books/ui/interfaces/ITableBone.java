package com.papao.books.ui.interfaces;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Menu;

public interface ITableBone {

    Menu createTableMenu();

    TableViewer getViewer();

}
