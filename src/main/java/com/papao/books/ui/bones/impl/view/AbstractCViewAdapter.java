package com.papao.books.ui.bones.impl.view;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Shell;

public abstract class AbstractCViewAdapter extends AbstractCView {

	/**
	 * @param parent
	 * @param viewMode
	 */
	public AbstractCViewAdapter(final Shell parent, final int viewMode) {
		super(parent, viewMode);
	}

	/**
	 * @param parent
	 * @param shellStyle
	 * @param viewMode
	 */
	public AbstractCViewAdapter(final Shell parent, final int shellStyle, final int viewMode) {
		super(parent, shellStyle, viewMode);
	}

	/**
	 * @param parent
	 * @param parentPos
	 * @param viewMode
	 */
	public AbstractCViewAdapter(final Shell parent, final Rectangle parentPos, final int viewMode) {
		super(parent, parentPos, viewMode);
	}

	/**
	 * @param parent
	 * @param shellStyle
	 * @param parentPos
	 * @param viewMode
	 */
	public AbstractCViewAdapter(final Shell parent,
								final int shellStyle,
								final Rectangle parentPos,
								final int viewMode) {
		super(parent, shellStyle, parentPos, viewMode);
	}

	/**
	 * @param parent
	 * @param shellStyle
	 * @param viewOptions
	 * @param parentPos
	 * @param viewMode
	 */
	public AbstractCViewAdapter(final Shell parent,
								final int shellStyle,
								final int viewOptions,
								final Rectangle parentPos,
								final int viewMode) {
		super(parent, shellStyle, viewOptions, parentPos, viewMode);
	}

	/**
	 * @param parent
	 * @param shellStyle
	 * @param viewOptions
	 * @param shellImg
	 * @param parentPos
	 * @param viewMode
	 */
	public AbstractCViewAdapter(final Shell parent,
								final int shellStyle,
								final int viewOptions,
								final Image shellImg,
								final Rectangle parentPos,
								final int viewMode) {
		super(parent, shellStyle, viewOptions, shellImg, parentPos, viewMode);
	}

	@Override
    protected boolean validate() {
		return false;
	}

	@Override
    protected void saveData() {
	}

}
