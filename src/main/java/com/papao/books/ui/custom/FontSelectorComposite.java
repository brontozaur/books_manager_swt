package com.papao.books.ui.custom;

import com.papao.books.ui.AppImages;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

public class FontSelectorComposite extends Composite implements Listener {

    private Text textInfo;
    private ToolItem itemSelectie;
    private Font font;

    public FontSelectorComposite(final Composite parent) {
        this(parent, false);
    }

    /**
     * @param drawLabelInfo
     *            creeaza un label cu textul "Font", daca este true
     */
    public FontSelectorComposite(final Composite parent, final boolean drawLabelInfo) {
        super(parent, SWT.NONE);

        if (drawLabelInfo) {
            new Label(this, SWT.NONE).setText("Font");
            GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).span(((GridLayout) parent.getLayout()).numColumns,
                    1).applyTo(this);
            GridLayoutFactory.swtDefaults().numColumns(3).equalWidth(false).margins(0, 0).applyTo(this);
        } else {
            GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).span(((GridLayout) parent.getLayout()).numColumns - 1,
                    1).applyTo(this);
            GridLayoutFactory.swtDefaults().numColumns(2).equalWidth(false).margins(0, 0).applyTo(this);
        }
        this.addListener(SWT.Dispose, this);

        addComponents();
    }

    private void addComponents() {
        this.textInfo = new Text(this, SWT.BORDER | SWT.READ_ONLY);
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).applyTo(this.textInfo);

        this.itemSelectie = new ToolItem(new ToolBar(this, SWT.FLAT), SWT.PUSH);
        GridDataFactory.fillDefaults().align(SWT.END, SWT.CENTER).grab(false, false).applyTo(this.itemSelectie.getParent());
        this.itemSelectie.setImage(AppImages.getImage16(AppImages.IMG_SEARCH));
        this.itemSelectie.setHotImage(AppImages.getImage16Focus(AppImages.IMG_SEARCH));
        this.itemSelectie.setToolTipText("Selecție font");
        this.itemSelectie.addListener(SWT.Selection, this);
    }

    @Override
    public final void handleEvent(final Event e) {
        if ((e.type == SWT.Selection) && (e.widget == this.itemSelectie)) {
            selectFont();
        }
        if (e.type == SWT.Dispose) {
            if ((this.font != null) && !this.font.isDisposed()) {
                this.font.dispose();
            }
        }
    }

    public final Font getSelectedFont() {
        return this.font;
    }

    public final void setNewFont(final Font font) {
        this.font = font;
        this.textInfo.setFont(this.font);
        if ((this.font != null) && !this.font.isDisposed()) {
            this.textInfo.setText(this.font.getFontData()[0].getName() + "," + this.font.getFontData()[0].getHeight());
        }
        layout();
        getParent().layout();
        this.textInfo.notifyListeners(SWT.FocusIn, new Event());
    }

    private void selectFont() {
        FontDialog fd = new FontDialog(this.getShell());
        if ((this.font != null) && !this.font.isDisposed()) {
            fd.setFontList(this.font.getFontData());
        }
        FontData fdata = fd.open();
        if ((fdata == null) || StringUtils.isEmpty(fdata.getName())) {
            return;
        }
        setNewFont(new Font(Display.getDefault(), fdata));
    }

    public final Text getTextSelectie() {
        return this.textInfo;
    }
}
