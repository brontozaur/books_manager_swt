package com.papao.books.view.custom;

import com.papao.books.view.AppImages;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

import java.io.File;

public class DirectorySelectorComposite extends Composite implements Listener {

    private Text textInfo;
    private ToolItem itemSelectie;
    private String dirPath;

    public DirectorySelectorComposite(final Composite parent) {
        this(parent, false);
    }

    /**
     * @param
     * @param drawLabelInfo
     *            creeaza un label cu textul "Fisier", daca este true
     */
    public DirectorySelectorComposite(final Composite parent, final boolean drawLabelInfo) {
        super(parent, SWT.NONE);

        if (drawLabelInfo) {
            new Label(this, SWT.NONE).setText("Director");
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
        this.itemSelectie.setToolTipText("Selectie director");
        this.itemSelectie.addListener(SWT.Selection, this);
    }

    @Override
    public final void handleEvent(final Event e) {
        if ((e.type == SWT.Selection) && (e.widget == this.itemSelectie)) {
            selectDir();
        }
    }

    public final String getSelectedDirPath() {
        if (this.dirPath.endsWith(File.separator)) {
            return this.dirPath;
        }
        return this.dirPath.concat(File.separator);
    }

    public final void setDirPath(final String dirPath) {
        this.dirPath = dirPath;
        this.textInfo.setText(this.dirPath);
        layout();
        getParent().layout();
        this.textInfo.notifyListeners(SWT.FocusIn, new Event());
    }

    private void selectDir() {
        DirectoryDialog dd = new DirectoryDialog(this.getShell());
        dd.setMessage("Selectati un director");
        dd.setText("Selectare director");
        if (StringUtils.isNotEmpty(this.dirPath)) {
            dd.setFilterPath(this.dirPath);
        }
        String selectedDir = dd.open();
        if (selectedDir == null) {
            return;
        }
        setDirPath(selectedDir);
    }

    public final Text getTextSelectie() {
        return this.textInfo;
    }

    public final ToolItem getItemSelectie() {
        return this.itemSelectie;
    }
}
