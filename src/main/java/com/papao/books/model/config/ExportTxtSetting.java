package com.papao.books.model.config;

import com.papao.books.view.auth.EncodeLive;
import org.bson.types.ObjectId;

public class ExportTxtSetting extends AbstractSetting {

    private String exportDir = EncodeLive.getReportsDir();
    private boolean automaticExportPath = true;
    private boolean showNrCrt = true;
    private boolean showTitle = true;
    private boolean hasBorder = true;

    public ExportTxtSetting() {
        this(EncodeLive.getIdUser(), EncodeLive.getCurrentUserName());
    }

    public ExportTxtSetting(ObjectId idUser, String numeUser) {
        super(idUser, numeUser, SettingType.EXPORT_TXT);
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

    public boolean isShowNrCrt() {
        return showNrCrt;
    }

    public void setShowNrCrt(boolean showNrCrt) {
        this.showNrCrt = showNrCrt;
    }

    public boolean isShowTitle() {
        return showTitle;
    }

    public void setShowTitle(boolean showTitle) {
        this.showTitle = showTitle;
    }

    public boolean isHasBorder() {
        return hasBorder;
    }

    public void setHasBorder(boolean hasBorder) {
        this.hasBorder = hasBorder;
    }

    public void reset() {
        exportDir = EncodeLive.getReportsDir();
        automaticExportPath = true;
        showNrCrt = true;
        showTitle = true;
        hasBorder = true;
    }

    @Override
    public String toString() {
        return "ExportTxtSetting{" +
                "exportDir='" + exportDir + '\'' +
                ", automaticExportPath=" + automaticExportPath +
                ", showNrCrt=" + showNrCrt +
                ", showTitle=" + showTitle +
                ", hasBorder=" + hasBorder +
                '}';
    }

    @Override
    public boolean isValid() {
        return true;
    }
}
