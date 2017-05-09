package com.papao.books.model;

import com.papao.books.ui.AppImages;
import org.bson.types.ObjectId;
import org.eclipse.swt.graphics.Image;

public final class BlankMongoDbObject extends AbstractMongoDB {

    private String name = "";
    private Image image = null;
    private ObjectId id;

	public BlankMongoDbObject(final String name) {
		super();
		this.name = name;
		this.id = new ObjectId();
	}

	public BlankMongoDbObject(final String name, final ObjectId id) {
        this.name = name;
        this.id = id;
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

    @Override
    public ObjectId getId() {
        return this.id;
    }
}
