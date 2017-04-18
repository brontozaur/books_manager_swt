package com.papao.books.view.bones.impl.view;

import com.papao.books.model.AbstractMongoDB;
import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Shell;

public abstract class AbstractCSaveView extends AbstractCView {

    private static final Logger logger = Logger.getLogger(AbstractCSaveView.class);

    private ObjectId objectId;
    private AbstractMongoDB editedObject;

    public AbstractCSaveView(Shell parent, int viewMode, ObjectId objectId) {
        this(parent, null, viewMode, objectId);
    }

    public AbstractCSaveView(Shell parent, Rectangle parentPos, int viewMode, ObjectId objectId) {
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

    protected ObjectId getIdObject() {
        return this.objectId;
    }

    protected void setIdObject(ObjectId idObject) {
        this.objectId = idObject;
        extractEditedObject();
    }

    protected AbstractMongoDB getEditedObject() {
        return editedObject;
    }

}
