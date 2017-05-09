/*******************************************************************************
 * Copyright (c) 2012 Laurent CARON.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Laurent CARON (laurent.caron at gmail dot com) - initial API and implementation
 *     Haixing Hu (https://github.com/Haixing-Hu/)  - Modification for personal use.
 *******************************************************************************/
package com.github.haixing_hu.swt.starrating;

import com.papao.books.ui.AppImages;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;

/**
 * Instances of this class represent a star displayed by the StarRating component.
 *
 * @author Laurent CARON
 * @author Haixing Hu
 */
class Star {

	boolean hover;
	boolean marked;
	Rectangle bounds;
	private Image defaultImage;
	private Image hoverImage;
	private Image selectedImage;
	private Image selectedHoverImage;
	private final StarRating parent;

  Star(final StarRating parent) {
    this.parent = parent;

    if (parent.getSizeOfStars() == StarRating.Size.BIG) {
      defaultImage = AppImages.getImage16(AppImages.STAR_GRAY_32);
      hoverImage = AppImages.getImage16(AppImages.STAR_GRAY_FOCUS_32);
      selectedImage = AppImages.getImage16(AppImages.STAR_MARK_32);
      selectedHoverImage = AppImages.getImage16(AppImages.STAR_MARK_FOCUS_32);
    } else {
		defaultImage = AppImages.getImage16(AppImages.STAR_GRAY_16);
		hoverImage = AppImages.getImage16(AppImages.STAR_GRAY_FOCUS_16);
		selectedImage = AppImages.getImage16(AppImages.STAR_MARK_16);
		selectedHoverImage = AppImages.getImage16(AppImages.STAR_MARK_FOCUS_16);
    }
  }

	void draw(final GC gc, final int x, final int y) {
		Image image;
		if (!this.parent.isEnabled()) {
			image = this.defaultImage;
		} else {
			if (this.marked) {
				if (this.hover) {
					image = this.selectedHoverImage;
				} else {
					image = this.selectedImage;
				}
			} else {
				if (this.hover) {
					image = this.hoverImage;
				} else {
					image = this.defaultImage;
				}
			}
		}
		gc.drawImage(image, x, y);
		final Rectangle rect = image.getBounds();
		this.bounds = new Rectangle(x, y, rect.width, rect.height);
	}
}
