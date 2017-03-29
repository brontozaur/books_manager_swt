package com.papao.books.ui.view;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Shell;

public abstract class AbstractCanvasView extends AbstractView {

    public AbstractCanvasView(final Shell parent, final int viewMode) {
        this(parent, SWT.MIN | SWT.MAX | SWT.CLOSE | SWT.RESIZE, viewMode);
    }

    public AbstractCanvasView(final Shell parent, final int shellStyle, final int viewMode) {
        this(parent, shellStyle, SWT.NONE, viewMode);
    }

    public AbstractCanvasView(final Shell parent, final int shellStyle, final int viewOptions, final int viewMode) {
        this(parent, shellStyle, viewOptions, null, viewMode);
    }

    public AbstractCanvasView(final Shell parent, final int shellStyle, final int viewOptions, final Image shellImg, final int viewMode) {
        super(parent, Canvas.class, viewMode);
        setShellStyle(shellStyle);
        setViewOptions(viewOptions);
        setShellImage(shellImg);
        /**
         * calling the createGUI() creates a new Graphical User Interface with the specified params.
         * Do NOT forget to call this in other implementations.
         */
        this.customizeView();
        super.createGUI();
    }

    public final Canvas getContainer() {
        return (Canvas) super.getWidget();
    }

}