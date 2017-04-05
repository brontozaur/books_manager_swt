package com.papao.books.view.bones.impl.view;

import com.papao.books.model.AbstractDB;
import org.apache.log4j.Logger;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Shell;

public abstract class AbstractCSaveView extends AbstractCView {

    private static final Logger logger = Logger.getLogger(AbstractCSaveView.class);

    private String objectId;
    private AbstractDB editedObject;

    protected abstract Class<? extends AbstractDB> getClazz();

    public AbstractCSaveView(Shell parent, int viewMode, String objectId) {
        this(parent, null, viewMode, objectId);
    }

    public AbstractCSaveView(Shell parent, Rectangle parentPos, int viewMode, String objectId) {
        super(parent, parentPos, viewMode);
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

    protected String getIdObject() {
        return this.objectId;
    }

    protected void setIdObject(String idObject) {
        this.objectId = idObject;
        extractEditedObject();
    }

    protected AbstractDB getEditedObject() {
        return editedObject;
    }

}
