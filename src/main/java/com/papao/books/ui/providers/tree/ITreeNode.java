package com.papao.books.ui.providers.tree;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;

import java.util.List;

public interface ITreeNode {

    List<ITreeNode> getChildrens();

    boolean hasChildrens();

    ITreeNode getParent();

    void setParent(ITreeNode parent);

    String getName();

    Image getImage();

    Color getForeground();

    Color getBackground();

    Font getFont();

    void createChildrens();
}
