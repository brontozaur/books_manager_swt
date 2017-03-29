package com.papao.books.ui.view;

import com.papao.books.model.AbstractDB;
import org.apache.log4j.Logger;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Shell;

public abstract class AbstractCSaveView extends AbstractCView {

    private static final Logger logger = Logger.getLogger(AbstractCSaveView.class);

    private long objectId;
    private AbstractDB editedObject;

    protected abstract Class<? extends AbstractDB> getClazz();

    public AbstractCSaveView(Shell parent, int viewMode, long objectId) {
        this(parent, null, viewMode, objectId);
    }

    public AbstractCSaveView(Shell parent, Rectangle parentPos, int viewMode, long objectId) {
        super(parent, parentPos, viewMode);
        this.objectId = objectId;

        extractEditedObject();
    }

    private void extractEditedObject() {
        try {

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    protected long getIdObject() {
        return this.objectId;
    }

    protected void setIdObject(long idObject) {
        this.objectId = idObject;
        extractEditedObject();
    }

    protected AbstractDB getEditedObject() {
        return editedObject;
    }

}
