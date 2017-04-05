package com.papao.books.view.custom;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

public class XButtonData {
    private Image image;
    private Image hotImage;
    private String mainText;
    private Color labelTextColor = Display.getDefault().getSystemColor(SWT.COLOR_BLACK);
    private int width;
    private int textAlignment;
    private Color borderColor = Display.getDefault().getSystemColor(SWT.COLOR_BLACK);
    private String toolTip = "";

    public Image getImage() {
        return this.image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public String getMainText() {
        if (this.mainText == null) {
            return "";
        }
        return this.mainText;
    }

    public void setMainText(String mainText) {
        this.mainText = mainText;
    }

    public Image getHotImage() {
        return this.hotImage;
    }

    public void setHotImage(Image hotImage) {
        this.hotImage = hotImage;
    }

    public Color getLabelTextColor() {
        return this.labelTextColor;
    }

    public void setLabelTextColor(Color labelTextColor) {
        this.labelTextColor = labelTextColor;
    }

    public int getWidth() {
        return this.width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getTextAlignment() {
        return this.textAlignment;
    }

    public void setTextAlignment(int textAlignment) {
        this.textAlignment = textAlignment;
    }

    public Color getBorderColor() {
        return this.borderColor;
    }

    public void setBorderColor(Color borderColor) {
        this.borderColor = borderColor;
    }

    public String getToolTip() {
        return this.toolTip;
    }

    public void setToolTip(String toolTip) {
        this.toolTip = toolTip;
    }

}