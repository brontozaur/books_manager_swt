package com.papao.books.model.config;

import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;

public class TableSetting extends AbstractSetting {

    private int[] widths;
    private int[] aligns;
    private boolean[] visibility;
    private String tableKey;

    public TableSetting(ObjectId idUser, String numeUser) {
        super(idUser, numeUser, SettingType.TABLE);
    }

    public int[] getWidths() {
        return widths;
    }

    public void setWidths(int[] widths) {
        this.widths = widths;
    }

    public int[] getAligns() {
        return aligns;
    }

    public void setAligns(int[] aligns) {
        this.aligns = aligns;
    }

    public boolean[] getVisibility() {
        return visibility;
    }

    public void setVisibility(boolean[] visibility) {
        this.visibility = visibility;
    }

    public String getTableKey() {
        return tableKey;
    }

    public void setTableKey(String tableKey) {
        this.tableKey = tableKey;
    }

    @Override
    public boolean isValid() {
        return StringUtils.isNotBlank(tableKey) &&
                widths != null && visibility != null && aligns != null
                && widths.length > 0 && visibility.length > 0 && aligns.length > 0
                && (widths.length == visibility.length && visibility.length == aligns.length);
    }
}
