package com.papao.books.model.config;

import com.papao.books.view.auth.EncodeLive;
import org.bson.types.ObjectId;
import org.eclipse.swt.SWT;

public class ExportHtmlSetting extends AbstractSetting {

    private String exportDir = EncodeLive.getReportsDir();
    private boolean automaticExportPath = true;
    private String fontName = "timesi.ttf";
    private String fontNameUser = "Times New Roman";
    private int fontSize = 10;
    private int fontStyle = SWT.NORMAL;
    private boolean showTitle = true;
    private boolean showNrCrt = true;
    private boolean showHeader = true;
    private boolean showGrayEffect = true;

    public ExportHtmlSetting() {
        this(EncodeLive.getIdUser(), EncodeLive.getCurrentUserName());
    }

    public ExportHtmlSetting(ObjectId idUser, String numeUser) {
        super(idUser, numeUser, SettingType.EXPORT_HTML);
    }

    public String getExportDir() {
        return exportDir;
    }

    public void setExportDir(String exportDir) {
        this.exportDir = exportDir;
    }

    public boolean isAutomaticExportPath() {
        return automaticExportPath;
    }

    public void setAutomaticExportPath(boolean automaticExportPath) {
        this.automaticExportPath = automaticExportPath;
    }

    public String getFontName() {
        return fontName;
    }

    public void setFontName(String fontName) {
        this.fontName = fontName;
    }

    public String getFontNameUser() {
        return fontNameUser;
    }

    public void setFontNameUser(String fontNameUser) {
        this.fontNameUser = fontNameUser;
    }

    public int getFontSize() {
        return fontSize;
    }

    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
    }

    public int getFontStyle() {
        return fontStyle;
    }

    public void setFontStyle(int fontStyle) {
        this.fontStyle = fontStyle;
    }

    public boolean isShowTitle() {
        return showTitle;
    }

    public void setShowTitle(boolean showTitle) {
        this.showTitle = showTitle;
    }

    public boolean isShowNrCrt() {
        return showNrCrt;
    }

    public void setShowNrCrt(boolean showNrCrt) {
        this.showNrCrt = showNrCrt;
    }

    public boolean isShowHeader() {
        return showHeader;
    }

    public void setShowHeader(boolean showHeader) {
        this.showHeader = showHeader;
    }

    public boolean isShowGrayEffect() {
        return showGrayEffect;
    }

    public void setShowGrayEffect(boolean showGrayEffect) {
        this.showGrayEffect = showGrayEffect;
    }

    @Override
    public String toString() {
        return "ExportHtmlSetting{" +
                "exportDir='" + exportDir + '\'' +
                ", automaticExportPath=" + automaticExportPath +
                ", fontName='" + fontName + '\'' +
                ", fontNameUser='" + fontNameUser + '\'' +
                ", fontSize=" + fontSize +
                ", fontStyle=" + fontStyle +
                ", showTitle=" + showTitle +
                ", showNrCrt=" + showNrCrt +
                ", showHeader=" + showHeader +
                ", showGrayEffect=" + showGrayEffect +
                '}';
    }

    @Override
    public boolean isValid() {
        return true;
    }

    public void reset() {
        exportDir = EncodeLive.getReportsDir();
        automaticExportPath = true;
        fontName = "timesi.ttf";
        fontNameUser = "Times New Roman";
        fontSize = 10;
        fontStyle = SWT.NORMAL;
        showTitle = true;
        showNrCrt = true;
        showHeader = true;
        showGrayEffect = true;
    }
}
