package com.papao.books.view.custom;

import com.papao.books.view.AppImages;
import com.papao.books.view.util.ColorUtil;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.*;

public class ClosableCanvas extends Canvas {

    private String text;

    public ClosableCanvas(final Composite parent, String text) {
        super(parent, SWT.NONE);
        this.text = text;

        GridLayoutFactory.fillDefaults().numColumns(2).extendedMargins(5, 1, 1, 1).equalWidth(false).applyTo(this);
        this.setData(text);

        new Label(this, SWT.NONE).setText(text);

        ToolItem itemClose = new ToolItem(new ToolBar(this, SWT.FLAT), SWT.NONE);
        itemClose.setImage(AppImages.getGrayImageMiscByName(AppImages.IMG_MISC_SIMPLE_X));
        itemClose.setHotImage(AppImages.getImageMiscByName(AppImages.IMG_MISC_SIMPLE_X_RED));
        itemClose.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                ClosableCanvas.this.dispose();
                parent.layout();
            }
        });
        parent.layout();

        this.addListener(SWT.Paint, new Listener() {

            @Override
            public void handleEvent(final Event e) {
                e.gc.setForeground(ColorUtil.COLOR_BLACK);
                e.gc.drawRoundRectangle(0,
                        0,
                        getClientArea().width - 1,
                        getClientArea().height - 1,
                        3,
                        3);

            }
        });
    }

    public String getText() {
        return this.text;
    }
}
