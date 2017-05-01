package com.papao.books.model.config;

import com.papao.books.export.xls.ExportXls;
import com.papao.books.view.auth.EncodeLive;
import org.bson.types.ObjectId;

public class ExportXlsSetting extends AbstractSetting {

    private String exportDir = EncodeLive.getReportsDir();
    private boolean automaticExportPath = true;
    private boolean showHeader = true;
    private String extension = ExportXls.XLS_EXTENSION;
    private boolean autoResizeCols = true;

    public ExportXlsSetting() {
        this(EncodeLive.getIdUser(), EncodeLive.getCurrentUserName());
    }

    public ExportXlsSetting(ObjectId idUser, String numeUser) {
        super(idUser, numeUser, SettingType.EXPORT_XLS);
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

    public boolean isShowHeader() {
        return showHeader;
    }

    public void setShowHeader(boolean showHeader) {
        this.showHeader = showHeader;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public boolean isAutoResizeCols() {
        return autoResizeCols;
    }

    public void setAutoResizeCols(boolean autoResizeCols) {
        this.autoResizeCols = autoResizeCols;
    }

    @Override
    public String toString() {
        return "ExportXlsSetting{" +
                "exportDir='" + exportDir + '\'' +
                ", automaticExportPath=" + automaticExportPath +
                ", showHeader=" + showHeader +
                ", extension='" + extension + '\'' +
                ", autoResizeCols=" + autoResizeCols +
                '}';
    }

    public void reset() {
        exportDir = EncodeLive.getReportsDir();
        automaticExportPath = true;
        showHeader = true;
        extension = ExportXls.XLS_EXTENSION;
        autoResizeCols = true;
    }

    @Override
    public boolean isValid() {
        return getIdUser() != null;
    }
}
