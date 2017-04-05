package com.papao.books.view.interfaces;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Menu;

public interface ITreeBone {

    TreeViewer getViewer();

    Menu createTreeDocsMenu();
}
