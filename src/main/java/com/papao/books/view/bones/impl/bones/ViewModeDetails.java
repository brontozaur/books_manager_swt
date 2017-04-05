package com.papao.books.view.bones.impl.bones;

import com.papao.books.model.AbstractDB;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;

public class ViewModeDetails {

    private String fkMethodName;
    private String uiMethodName;
    private String imageMethodName;
    private Font font;
    private Color color;
    private Image globalImage;
    private boolean addAllNode = true;
    private boolean addRecentOpNode = true;
    private Class<? extends AbstractDB> fkClass;
    private TreeViewer treeViewer;

    public final static String ALL_STR = "Lista completa";

    public String getUiMethodName() {
        return this.uiMethodName;
    }

    public void setUiMethodName(final String uiMethodName) {
        this.uiMethodName = uiMethodName;
    }

    public String getImageMethodName() {
        return this.imageMethodName;
    }

    public void setImageMethodName(final String imageMethodName) {
        this.imageMethodName = imageMethodName;
    }

    public Font getFont() {
        return this.font;
    }

    public void setFont(final Font font) {
        this.font = font;
    }

    public Color getColor() {
        return this.color;
    }

    public void setColor(final Color color) {
        this.color = color;
    }

    public String getFkMethodName() {
        return this.fkMethodName;
    }

    public void setFkMethodName(final String fkMethodName) {
        this.fkMethodName = fkMethodName;
    }

    public Image getGlobalImage() {
        return this.globalImage;
    }

    public void setGlobalImage(final Image globalImage) {
        this.globalImage = globalImage;
    }

    public final boolean isAddAllNode() {
        return this.addAllNode;
    }

    public final void setAddAllNode(final boolean addAllNode) {
        this.addAllNode = addAllNode;
    }

    public final boolean isAddRecentOpNode() {
        return this.addRecentOpNode;
    }

    public final void setAddRecentOpNode(final boolean addRecentOpNode) {
        this.addRecentOpNode = addRecentOpNode;
    }

    public final Class<? extends AbstractDB> getFkClass() {
        return this.fkClass;
    }

    public final void setFkClass(Class<? extends AbstractDB> fkClass) {
        this.fkClass = fkClass;
    }

    public final TreeViewer getTreeViewer() {
        return this.treeViewer;
    }

    public final void setTreeViewer(TreeViewer treeViewer) {
        this.treeViewer = treeViewer;
    }

}
