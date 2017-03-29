package com.papao.books.ui.interfaces;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Menu;

public interface ITreeBone {

    TreeViewer getViewer();

    Menu createTreeDocsMenu();
}
