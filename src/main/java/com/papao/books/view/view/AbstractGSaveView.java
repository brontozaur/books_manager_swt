package com.papao.books.view.view;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Shell;

public abstract class AbstractGSaveView extends AbstractGView {

    private static final Logger logger = Logger.getLogger(AbstractGSaveView.class);

    public AbstractGSaveView(Shell parent, int viewMode) {
        this(parent, SWT.MIN | SWT.CLOSE | SWT.MAX, viewMode);
    }

    public AbstractGSaveView(Shell parent, int shellStyle, int viewMode) {
        this(parent, shellStyle, null, viewMode);

    }

    public AbstractGSaveView(Shell parent, int shellStyle, Rectangle parentPos, int viewMode) {
        super(parent, shellStyle, parentPos, viewMode);
    }

}
