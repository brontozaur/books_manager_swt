package com.papao.books.model;

import com.papao.books.ui.AppImages;
import org.apache.log4j.Logger;
import org.eclipse.swt.graphics.Image;

public final class BlankDbObject extends AbstractDBDummy {

    private String name = "";
    private Image image = null;

    public static final String TABLE_NAME = "";

    public static final String EXTERNAL_REFLECT_GET_NAME = "getName";
    public static final String EXTERNAL_REFLECT_GET_IMAGE = "getImage";

	private static Logger logger = Logger.getLogger(BlankDbObject.class);

	public BlankDbObject(final String name) {
		super();
		setName(name);
	}

	public BlankDbObject(final String name, final long id) {
		super(id);
        setName(name);
    }

    @Override
    public BlankDbObject cloneObject() {
        BlankDbObject clone = null;
        try {
            clone = (BlankDbObject) this.clone();
        } catch (CloneNotSupportedException exc) {
			logger.error(exc.getMessage(), exc);
        }
        return clone;
    }

    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public Image getImage() {
        if ((this.image == null) || this.image.isDisposed()) {
            this.image = AppImages.IMAGE_NOT_FOUND_16X16;
        }
        return this.image;
    }

    public void setImage(final Image image) {
        this.image = image;
    }
}
