package com.papao.books.model.config;

import com.papao.books.view.auth.EncodeLive;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;

public class WindowSetting extends AbstractSetting {

    private int x;
    private int y;
    private int width;
    private int height;
    private String windowKey;

    public WindowSetting() {
        this(EncodeLive.getIdUser(), EncodeLive.getCurrentUserName());
    }

    public WindowSetting(ObjectId idUser, String numeUser) {
        super(idUser, numeUser, SettingType.WINDOW);
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getWindowKey() {
        return windowKey;
    }

    public void setWindowKey(String windowKey) {
        this.windowKey = windowKey;
    }

    @Override
    public String toString() {
        return "WindowSetting{" +
                "x=" + x +
                ", y=" + y +
                ", width=" + width +
                ", height=" + height +
                ", windowKey='" + windowKey + '\'' +
                '}';
    }

    @Override
    public boolean isValid() {
        return StringUtils.isNotBlank(windowKey) && width > 0 && height > 0;
    }
}
