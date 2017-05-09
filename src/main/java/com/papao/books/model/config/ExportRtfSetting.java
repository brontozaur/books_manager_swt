package com.papao.books.model.config;

import com.papao.books.ui.auth.EncodeLive;
import org.bson.types.ObjectId;
import org.eclipse.swt.SWT;

public class ExportRtfSetting extends AbstractSetting {

    public final static String PORTRAIT = "Normal (Portrait)";
    public final static String LANDSCAPE = "Panoramic (Landscape)";
    public final static String[] ORIENTATIONS = new String[] {
            PORTRAIT, LANDSCAPE };

    private String exportDir = EncodeLive.getReportsDir();
    private boolean automaticExportPath = true;
    private String pageOrientation = PORTRAIT;
    private String pageSize = "A4";
    private String fontName = "timesi.ttf";
    private String fontNameUser = "Times New Roman";
    private int fontSize = 10;
    private int fontStyle = SWT.NORMAL;
    private boolean showTitle = true;
    private boolean showNrCrt = true;
    private boolean showHeader = true;
    private boolean showGrayEffect = true;
    private boolean showPageNumber = true;

    public ExportRtfSetting() {
        this(EncodeLive.getIdUser(), EncodeLive.getCurrentUserName());
    }

    public ExportRtfSetting(ObjectId idUser, String numeUser) {
        super(idUser, numeUser, SettingType.EXPORT_RTF);
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

    public String getPageOrientation() {
        return pageOrientation;
    }

    public void setPageOrientation(String pageOrientation) {
        this.pageOrientation = pageOrientation;
    }

    public String getPageSize() {
        return pageSize;
    }

    public void setPageSize(String pageSize) {
        this.pageSize = pageSize;
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

    public boolean isShowPageNumber() {
        return showPageNumber;
    }

    public void setShowPageNumber(boolean showPageNumber) {
        this.showPageNumber = showPageNumber;
    }

    @Override
    public String toString() {
        return "ExportRtfSetting{" +
                "exportDir='" + exportDir + '\'' +
                ", automaticExportPath=" + automaticExportPath +
                ", pageOrientation='" + pageOrientation + '\'' +
                ", pageSize='" + pageSize + '\'' +
                ", fontName='" + fontName + '\'' +
                ", fontNameUser='" + fontNameUser + '\'' +
                ", fontSize=" + fontSize +
                ", fontStyle=" + fontStyle +
                ", showTitle=" + showTitle +
                ", showNrCrt=" + showNrCrt +
                ", showHeader=" + showHeader +
                ", showGrayEffect=" + showGrayEffect +
                ", showPageNumber=" + showPageNumber +
                '}';
    }

    @Override
    public boolean isValid() {
        return getIdUser() != null;
    }

    public void reset() {
        exportDir = EncodeLive.getReportsDir();
        automaticExportPath = true;
        pageOrientation = PORTRAIT;
        pageSize = "A4";
        fontName = "timesi.ttf";
        fontNameUser = "Times New Roman";
        fontSize = 10;
        fontStyle = SWT.NORMAL;
        showTitle = true;
        showNrCrt = true;
        showHeader = true;
        showGrayEffect = true;
        showPageNumber = true;
    }
}
