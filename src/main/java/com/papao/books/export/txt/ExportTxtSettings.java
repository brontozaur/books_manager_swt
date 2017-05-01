package com.papao.books.export.txt;

import com.papao.books.view.view.SWTeXtension;
import org.apache.log4j.Logger;
import org.eclipse.swt.widgets.Table;

public final class ExportTxtSettings implements Cloneable {

    private static final Logger logger = Logger.getLogger(ExportTxtSettings.class);

    private String numeFisier;
    private Table swtTable;
    private Class<?> clazz;
    private String tableKey;
    private String titlu;
    private int nrOfItems;
    private boolean[] selection;
    private int[] dims;
    private int[] aligns;
    private int[] order;

    public ExportTxtSettings() {

    }

    public ExportTxtSettings cloneObject() {
        ExportTxtSettings clone = null;
        try {
            clone = (ExportTxtSettings) this.clone();
        } catch (CloneNotSupportedException exc) {
            logger.fatal(exc, exc);
            SWTeXtension.displayMessageEGeneric(exc);
        }
        return clone;
    }

    public String getNumeFisier() {
        return this.numeFisier;
    }

    public void setNumeFisier(final String numeFisier) {
        this.numeFisier = numeFisier;
    }

    public Table getSwtTable() {
        return this.swtTable;
    }

    public void setSwtTable(final Table swtTable) {
        this.swtTable = swtTable;
    }

    public Class<?> getClazz() {
        return this.clazz;
    }

    public void setClazz(final Class<?> clazz) {
        this.clazz = clazz;
    }

    public String getTableKey() {
        return this.tableKey;
    }

    public void setTableKey(final String tableKey) {
        this.tableKey = tableKey;
    }

    public String getTitlu() {
        return this.titlu;
    }

    public void setTitlu(final String titlu) {
        this.titlu = titlu;
    }

    public int getNrOfItems() {
        return this.nrOfItems;
    }

    public void setNrOfItems(final int nrOfItems) {
        this.nrOfItems = nrOfItems;
    }

    public boolean[] getSelection() {
        return this.selection;
    }

    public void setSelection(final boolean[] selection) {
        this.selection = selection;
    }

    public int[] getDims() {
        return this.dims;
    }

    public void setDims(final int[] dims) {
        this.dims = dims;
    }

    public int[] getAligns() {
        return this.aligns;
    }

    public void setAligns(final int[] aligns) {
        this.aligns = aligns;
    }

    public int[] getOrder() {
        return this.order;
    }

    public void setOrder(final int[] order) {
        this.order = order;
    }
}