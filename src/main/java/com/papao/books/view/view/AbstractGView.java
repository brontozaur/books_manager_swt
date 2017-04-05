package com.papao.books.view.view;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;

public abstract class AbstractGView extends AbstractView {

    public AbstractGView(final Shell parent, final int viewMode) {
        this(parent, SWT.MIN | SWT.MAX | SWT.CLOSE | SWT.RESIZE, null, viewMode);
    }

    public AbstractGView(final Shell parent, final int shellStyle, final int viewMode) {
        this(parent, shellStyle, null, viewMode);
    }

    public AbstractGView(final Shell parent, final Rectangle parentPos, final int viewMode) {
        this(parent, SWT.MIN | SWT.MAX | SWT.CLOSE | SWT.RESIZE, parentPos, viewMode);
    }

    public AbstractGView(final Shell parent, final int shellStyle, final Rectangle parentPos, final int viewMode) {
        this(parent, shellStyle, SWT.NONE, parentPos, viewMode);
    }

    public AbstractGView(final Shell parent, final int shellStyle, final int viewOptions, final Rectangle parentPos, final int viewMode) {
        this(parent, shellStyle, viewOptions, null, parentPos, viewMode);
    }

    public AbstractGView(final Shell parent,
                         final int shellStyle,
                         final int viewOptions,
                         final Image shellImg,
                         final Rectangle parentPos,
                         final int viewMode) {
        super(parent, Group.class, parentPos, viewMode);
        setShellStyle(shellStyle);
        setViewOptions(viewOptions);
        setShellImage(shellImg);
        setViewMode(viewMode);

        /**
         * calling the createGUI() creates a new Graphical User Interface with the specified params.
         * Do NOT forget to call this in other implementations.
         */
        this.customizeView();
        super.createGUI();
    }

    public final Group getContainer() {
        return (Group) super.getWidget();
    }

}