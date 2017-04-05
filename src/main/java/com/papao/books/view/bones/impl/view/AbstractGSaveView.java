package com.papao.books.view.bones.impl.view;

import com.papao.books.model.AbstractDB;
import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Shell;

public abstract class AbstractGSaveView extends AbstractGView {

    private static final Logger logger = Logger.getLogger(AbstractGSaveView.class);

    private long objectId;
    private AbstractDB editedObject;

    protected abstract Class<? extends AbstractDB> getClazz();

    public AbstractGSaveView(Shell parent, int viewMode, long objectId) {
        this(parent, SWT.MIN | SWT.CLOSE | SWT.MAX, viewMode, objectId);
    }

    public AbstractGSaveView(Shell parent, int shellStyle, int viewMode, long objectId) {
        this(parent, shellStyle, objectId, null, viewMode);

    }

    public AbstractGSaveView(Shell parent, int shellStyle, long objectId, Rectangle parentPos, int viewMode) {
        super(parent, shellStyle, parentPos, viewMode);
        this.objectId = objectId;

        extractEditedObject();
    }

    private void extractEditedObject() {
//        try {
//            if (this.objectId > 0) {
//                this.editedObject = Database.getDbObjectById(getClazz(), this.objectId);
//            } else {
//                this.editedObject = getClazz().newInstance();
//            }
//        } catch (Exception e) {
//            SQLLibrary.processErr(e, logger);
//        }
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
