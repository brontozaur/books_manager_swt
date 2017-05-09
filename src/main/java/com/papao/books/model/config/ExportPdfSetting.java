package com.papao.books.model.config;

import com.papao.books.ui.auth.EncodeLive;
import org.bson.types.ObjectId;
import org.eclipse.swt.SWT;

public class ExportPdfSetting extends AbstractSetting {

    public final static String[] VERSIONS = new String[]{
            "1.2 (Acrobat 3.0+)", "1.3 (Acrobat 4.0+)", "1.4 (Acrobat 5.0+)", "1.5 (Acrobat 6.0+)", "1.6 (Acrobat 7.0+)", "1.7 (Acrobat 8.0+)"};
    public final static String PORTRAIT = "Normal (Portrait)";
    public final static String LANDSCAPE = "Panoramic (Landscape)";
    public final static String[] ORIENTATIONS = new String[]{PORTRAIT, LANDSCAPE};

    private String exportDir = EncodeLive.getReportsDir();
    private boolean automaticExportPath = true;
    private String pageOrientation = PORTRAIT;
    private String pageSize = "A4";
    private String docVersion = VERSIONS[0];
    private String fontName = "timesi.ttf";
    private String fontNameUser = "Times New Roman";
    private int fontSize = 10;
    private int fontStyle = SWT.NORMAL;
    private int compression = 9;
    private boolean showTitle = true;
    private boolean showNrCrt = true;
    private boolean showHeader = true;
    private boolean showGrayEffect = true;
    private boolean showPageNumber = true;

    public ExportPdfSetting() {
        this(EncodeLive.getIdUser(), EncodeLive.getCurrentUserName());
    }

    public ExportPdfSetting(ObjectId idUser, String numeUser) {
        super(idUser, numeUser, SettingType.EXPORT_PDF);
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

    public String getDocVersion() {
        return docVersion;
    }

    public void setDocVersion(String docVersion) {
        this.docVersion = docVersion;
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

    public int getCompression() {
        return compression;
    }

    public void setCompression(int compression) {
        this.compression = compression;
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
        return "ExportPdfSetting{" +
                "exportDir='" + exportDir + '\'' +
                ", automaticExportPath=" + automaticExportPath +
                ", pageOrientation='" + pageOrientation + '\'' +
                ", pageSize='" + pageSize + '\'' +
                ", docVersion='" + docVersion + '\'' +
                ", fontName='" + fontName + '\'' +
                ", fontNameUser='" + fontNameUser + '\'' +
                ", fontSize=" + fontSize +
                ", fontStyle=" + fontStyle +
                ", compression=" + compression +
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
        docVersion = VERSIONS[0];
        fontName = "timesi.ttf";
        fontNameUser = "Times New Roman";
        fontSize = 10;
        fontStyle = SWT.NORMAL;
        compression = 9;
        showTitle = true;
        showNrCrt = true;
        showHeader = true;
        showGrayEffect = true;
        showPageNumber = true;
    }
}
