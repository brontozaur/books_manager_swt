package com.papao.books.view.custom;

import com.papao.books.view.AppImages;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

public class FileSelectorComposite extends Composite implements Listener {

    private Text textInfo;
    private ToolItem itemSelectie;
    private String filePath;
    private String[] filterExtensions;
    private String[] filterNames;

    public FileSelectorComposite(final Composite parent) {
        this(parent, false, null, null);
    }

    /**
     * @param parintele
     *            componentei
     * @param drawLabelInfo
     *            creeaza un label cu textul "Fisier", daca este true
     */
    public FileSelectorComposite(final Composite parent, final boolean drawLabelInfo, final String[] filterExtensions, final String[] filterNames) {
        super(parent, SWT.NONE);
        this.filterExtensions = filterExtensions;
        this.filterNames = filterNames;

        if (drawLabelInfo) {
            new Label(this, SWT.NONE).setText("Fisier");
            GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).span(((GridLayout) parent.getLayout()).numColumns,
                    1).applyTo(this);
            GridLayoutFactory.swtDefaults().numColumns(3).equalWidth(false).margins(0, 0).applyTo(this);
        } else {
            GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).span(((GridLayout) parent.getLayout()).numColumns - 1,
                    1).applyTo(this);
            GridLayoutFactory.swtDefaults().numColumns(2).equalWidth(false).margins(0, 0).applyTo(this);
        }
        addComponents();
    }

    private void addComponents() {
        this.textInfo = new Text(this, SWT.BORDER | SWT.READ_ONLY);
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).applyTo(this.textInfo);

        this.itemSelectie = new ToolItem(new ToolBar(this, SWT.FLAT), SWT.PUSH);
        GridDataFactory.fillDefaults().align(SWT.END, SWT.CENTER).grab(false, false).applyTo(this.itemSelectie.getParent());
        this.itemSelectie.setImage(AppImages.getImage16(AppImages.IMG_SEARCH));
        this.itemSelectie.setHotImage(AppImages.getImage16Focus(AppImages.IMG_SEARCH));
        this.itemSelectie.setToolTipText("Selectie fisier");
        this.itemSelectie.addListener(SWT.Selection, this);
    }

    @Override
    public final void handleEvent(final Event e) {
        if ((e.type == SWT.Selection) && (e.widget == this.itemSelectie)) {
            selectFile();
        }
    }

    public final String getSelectedFilePath() {
        return this.filePath;
    }

    public final void setFilePath(final String filePath) {
        this.filePath = filePath;
        this.textInfo.setText(this.filePath);
        layout();
        getParent().layout();
        this.textInfo.notifyListeners(SWT.FocusIn, new Event());
    }

    private void selectFile() {
        FileDialog fd = new FileDialog(this.getShell());
        if (this.filterExtensions != null) {
            fd.setFilterExtensions(this.filterExtensions);
            fd.setFilterNames(this.filterNames);
        }
        String selectedFile = fd.open();
        if (selectedFile == null) {
            return;
        }
        setFilePath(selectedFile);
    }

    public final Text getTextSelectie() {
        return this.textInfo;
    }
}
