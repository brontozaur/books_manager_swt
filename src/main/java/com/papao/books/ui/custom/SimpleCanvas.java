package com.papao.books.ui.custom;

import com.papao.books.ui.util.ColorUtil;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.*;

public class SimpleCanvas extends Canvas {

    private String text;
    private Label textLabel;

    public SimpleCanvas(final Composite parent, String text) {
        super(parent, SWT.NONE);
        this.text = text;

        GridLayoutFactory.fillDefaults().numColumns(2).extendedMargins(5, 1, 1, 1).equalWidth(false).applyTo(this);
        this.setData(text);

        this.textLabel = new Label(this, SWT.NONE);
        this.textLabel.setText(text);

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

    public void setText(String text) {
        this.text = text;
        this.textLabel.setText(text);
    }

    public String getText() {
        return this.text;
    }

}
