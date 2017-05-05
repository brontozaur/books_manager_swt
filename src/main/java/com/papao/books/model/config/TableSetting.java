package com.papao.books.model.config;

import com.papao.books.view.auth.EncodeLive;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.eclipse.swt.SWT;

import java.util.Arrays;

public class TableSetting extends AbstractSetting {

    private int[] widths;
    private int[] aligns;
    private int[] order;
    private boolean[] visibility;
    private String clazz;
    private String tableKey;
    private int nrOfColumns;

    public TableSetting() {
        this(0, "", "");
    }

    public TableSetting(int nrOfColumns, String clazz, String tableKey) {
        this(nrOfColumns, clazz, tableKey, EncodeLive.getIdUser(), EncodeLive.getCurrentUserName());
    }

    public TableSetting(int nrOfColumns, String clazz, String tableKey, ObjectId idUser, String numeUser) {
        super(idUser, numeUser, SettingType.TABLE);
        this.nrOfColumns = nrOfColumns;
        this.clazz = clazz;
        this.tableKey = tableKey;
        widths = new int[nrOfColumns];
        Arrays.fill(widths, 100);
        aligns = new int[nrOfColumns];
        Arrays.fill(aligns, SWT.LEFT);
        order = new int[nrOfColumns];
        for (int i = 0; i < nrOfColumns; i++) {
            order[i] = i;
        }
        visibility = new boolean[nrOfColumns];
        Arrays.fill(visibility, true);
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

    public int[] getOrder() {
        return order;
    }

    public void setOrder(int[] order) {
        this.order = order;
    }

    public String getTableKey() {
        return tableKey;
    }

    public void setTableKey(String tableKey) {
        this.tableKey = tableKey;
    }

    public String getClazz() {
        return clazz;
    }

    public void setClazz(String clazz) {
        this.clazz = clazz;
    }

    public int getNrOfColumns() {
        return nrOfColumns;
    }

    public void setNrOfColumns(int nrOfColumns) {
        this.nrOfColumns = nrOfColumns;
    }

    @Override
    public boolean isValid() {
        return StringUtils.isNotBlank(tableKey) &&
                StringUtils.isNotBlank(clazz) && nrOfColumns > 0 && getIdUser() != null
                && nrOfColumns == widths.length;
    }

    public int[] getCorrectDims() {
        int[] dd = new int[nrOfColumns];
        for (int i = 0; i < order.length; i++) {
            dd[i] = widths[order[i]];
        }
        return dd;
    }

    public int[] getCorrectAligns() {
        int[] dd = new int[nrOfColumns];
        for (int i = 0; i < order.length; i++) {
            dd[i] = aligns[order[i]];
        }
        return dd;
    }

    public boolean[] getCorrectVisible() {
        boolean[] dd = new boolean[nrOfColumns];
        for (int i = 0; i < order.length; i++) {
            dd[i] = visibility[order[i]];
        }
        return dd;
    }

    @Override
    public String toString() {
        return "TableSetting{" +
                "widths=" + Arrays.toString(widths) +
                ", aligns=" + Arrays.toString(aligns) +
                ", order=" + Arrays.toString(order) +
                ", visibility=" + Arrays.toString(visibility) +
                ", clazz='" + clazz + '\'' +
                ", tableKey='" + tableKey + '\'' +
                ", nrOfColumns=" + nrOfColumns +
                '}';
    }
}
