package com.papao.books.ui.custom;

import com.papao.books.ui.util.ColorUtil;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Listener;

public class SimpleCanvas extends Canvas {

    private final String text;
    final Link link;

    public SimpleCanvas(final Composite parent, String text) {
        super(parent, SWT.NONE);
        this.text = text;

        GridLayoutFactory.fillDefaults().numColumns(2).extendedMargins(5, 1, 1, 1).equalWidth(false).applyTo(this);
        this.setData(text);

        link = new Link(this, SWT.NONE);
        link.setText("<a>" + text + "</a>");
        link.setData(text);

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

    public Link getLink() {
        return link;
    }
}
