package com.papao.books.ui.custom;

import com.papao.books.ui.AppImages;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.*;

public class XButton extends Composite implements Listener {

    private final XButtonData data;
    private Label labelImage;
    private Label labelMainText;
    private final GC gc;
    private boolean selected;

    public XButton(final Composite parent, final XButtonData data) {
        super(parent, SWT.NONE);

        this.data = data;
        this.gc = new GC(this);

        if ((data.getTextAlignment() == SWT.RIGHT) || (data.getTextAlignment() == SWT.END)) {
            GridLayoutFactory.fillDefaults().numColumns(2).equalWidth(false).spacing(5, 0).margins(5, 5).applyTo(this);
            GridDataFactory.fillDefaults().grab(false, false).align(SWT.BEGINNING, SWT.CENTER).hint(data.getWidth(), SWT.DEFAULT).applyTo(this);
        } else {
            GridLayoutFactory.fillDefaults().numColumns(1).equalWidth(false).spacing(2, 0).margins(5, 5).applyTo(this);
            GridDataFactory.fillDefaults().grab(false, false).align(SWT.CENTER, SWT.CENTER).hint(data.getWidth(), SWT.DEFAULT).applyTo(this);
        }

        addComponents();

    }

    private void addComponents() {
        this.labelImage = new Label(this, SWT.NONE);
        this.labelImage.setImage(this.data.getImage());
        this.labelImage.addListener(SWT.MouseEnter, this);
        this.labelImage.addListener(SWT.MouseExit, this);

        this.labelMainText = new Label(this, SWT.NONE);
        this.labelMainText.setText(this.data.getMainText());
        this.labelMainText.setForeground(this.data.getLabelTextColor());
        this.labelMainText.addListener(SWT.MouseEnter, this);
        this.labelMainText.addListener(SWT.MouseExit, this);

        if ((this.data.getTextAlignment() == SWT.RIGHT) || (this.data.getTextAlignment() == SWT.END)) {
            GridDataFactory.fillDefaults().grab(false, true).align(SWT.BEGINNING, SWT.CENTER).applyTo(this.labelImage);
            GridDataFactory.fillDefaults().grab(false, true).align(SWT.BEGINNING, SWT.CENTER).applyTo(this.labelMainText);
        } else {
            GridDataFactory.fillDefaults().grab(true, true).align(SWT.CENTER, SWT.CENTER).applyTo(this.labelImage);
            GridDataFactory.fillDefaults().grab(true, true).align(SWT.CENTER, SWT.CENTER).applyTo(this.labelMainText);
        }

        this.addListener(SWT.MouseEnter, this);
        this.addListener(SWT.MouseExit, this);
        this.addListener(SWT.Dispose, this);
        this.addListener(SWT.Paint, this);

        this.labelImage.setImage(this.data.getImage());

        if (!this.data.getToolTip().isEmpty()) {
            this.labelImage.setToolTipText(this.data.getToolTip());
            this.labelMainText.setToolTipText(this.data.getToolTip());
            setToolTipText(this.data.getToolTip());
        }
    }

    public final void registerListeners(final int type, Listener lis) {
        this.addListener(type, lis);
        this.labelImage.addListener(type, lis);
        this.labelMainText.addListener(type, lis);
    }

    public final void unregisterListeners(final int type, Listener lis) {
        this.removeListener(type, lis);
        this.labelImage.removeListener(type, lis);
        this.labelMainText.removeListener(type, lis);
    }

    public final void setText(final String text) {
        if (text == null) {
            return;
        }
        this.labelMainText.setText(text);
        layout();
    }

    @Override
    public void handleEvent(final Event e) {
        if (e.type == SWT.MouseEnter) {
            this.labelImage.setImage(this.data.getHotImage());
            this.gc.setForeground(this.data.getBorderColor());
            this.gc.drawRoundRectangle(0, 1, getClientArea().width - 3, getClientArea().height - 3, 8, 8);
        } else if (e.type == SWT.MouseExit) {
            if (isSelected()) {
                return;
            }
            this.labelImage.setImage(this.data.getImage());
            if (getBackground() != null) {
                this.gc.setForeground(getBackground());
            } else if ((getParent().getBackground() != null) && (getParent().getBackgroundMode() != SWT.INHERIT_NONE)) {
                this.gc.setForeground(getParent().getBackground());
            } else {
                this.gc.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
            }
            this.gc.drawRoundRectangle(0, 1, getClientArea().width - 3, getClientArea().height - 3, 8, 8);
        } else if (e.type == SWT.Dispose) {
            this.gc.dispose();
        } else if (e.type == SWT.Paint) {
            if (!isSelected()) {
                return;
            }
            e.type = SWT.MouseEnter;
            notifyListeners(SWT.MouseEnter, e);
        }
    }

    public void setSelection() {
        this.selected = true;
        final Control[] ctr = getParent().getChildren();
        for (Control c : ctr) {
            if (!(c instanceof XButton) || (c == this)) {
                continue;
            }
            XButton xb = (XButton) c;
            xb.setSelected(false);
            xb.notifyListeners(SWT.MouseExit, new Event());
        }
        notifyListeners(SWT.MouseEnter, new Event());
    }

    public final String getMainText() {
        return this.data.getMainText();
    }

    public boolean isSelected() {
        return this.selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @Override
    public void setEnabled(boolean enable) {
        if (enable) {
            this.labelImage.setImage(this.data.getImage());
        } else {
            this.labelImage.setImage(AppImages.getGrayImage(this.data.getImage()));
        }
        super.setEnabled(enable);
    }

}
