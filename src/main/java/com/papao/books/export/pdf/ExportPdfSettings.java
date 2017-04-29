package com.papao.books.export.pdf;

import com.papao.books.view.view.SWTeXtension;
import org.apache.log4j.Logger;
import org.eclipse.swt.widgets.Table;

public final class ExportPdfSettings implements Cloneable {

    private static final Logger logger = Logger.getLogger(ExportPdfSettings.class);
    private String numeFisier;
    private Table swtTable;
    private Class<?> clazz;
    private String sufix;
    private String titlu;
    private int nrOfItems;
    private boolean[] selection;
    private int[] dims;
    private int[] aligns;
    private int[] order;
    private com.itextpdf.text.Rectangle pageSize;
    private com.itextpdf.text.pdf.PdfName pdfVersion;
    private com.itextpdf.text.Font font;

    public ExportPdfSettings() {

    }

    public ExportPdfSettings cloneObject() {
        ExportPdfSettings clone = null;
        try {
            clone = (ExportPdfSettings) this.clone();
        } catch (CloneNotSupportedException exc) {
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

    public String getSufix() {
        return this.sufix;
    }

    public void setSufix(final String sufix) {
        this.sufix = sufix;
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

    public com.itextpdf.text.Rectangle getPageSize() {
        return this.pageSize;
    }

    public void setPageSize(final com.itextpdf.text.Rectangle pageSize) {
        this.pageSize = pageSize;
    }

    public com.itextpdf.text.pdf.PdfName getPdfVersion() {
        return this.pdfVersion;
    }

    public void setPdfVersion(final com.itextpdf.text.pdf.PdfName pdfVersion) {
        this.pdfVersion = pdfVersion;
    }

    public com.itextpdf.text.Font getFont() {
        return this.font;
    }

    public void setFont(final com.itextpdf.text.Font font) {
        this.font = font;
    }
}