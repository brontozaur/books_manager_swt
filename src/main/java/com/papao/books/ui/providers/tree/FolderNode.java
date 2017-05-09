package com.papao.books.ui.providers.tree;

import com.papao.books.ui.AppImages;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

public class FolderNode implements ITreeNode {

	private final File folder;
	private ITreeNode parent;
	private List<ITreeNode> childrens;

	public FolderNode(final File folder) {
		this(null, folder);
	}

	public FolderNode(final ITreeNode parent, final File folder) {
		setParent(parent);
		this.folder = folder;
	}

	@Override
	public String getName() {
		return this.folder.getName();
	}

	public File getFolder() {
		return this.folder;
	}

	@Override
	public boolean hasChildrens() {
		return (getChildrens() != null) && !getChildrens().isEmpty();
	}

	public FolderNode getChildren(final String childName) {
		if ((getChildrens() == null) || (childName == null)) {
			return null;
		}
		for (ITreeNode child : getChildrens()) {
			if ((child != null) && child.getName().equals(childName)) {
				return (FolderNode) child;
			}
		}
		return null;
	}

	@Override
	public List<ITreeNode> getChildrens() {
		if (this.childrens == null) {
			createChildrens();
		}

		return this.childrens;
	}

	@Override
	public ITreeNode getParent() {
		return this.parent;
	}

	@Override
	public void setParent(final ITreeNode parent) {
		this.parent = parent;
	}

	@Override
	public void createChildrens() {
		if (this.childrens == null) {
			this.childrens = new ArrayList<ITreeNode>();
		}
		this.childrens.clear();
		FileFilter filtru = new FileFilter() {
			@Override
			public boolean accept(final File pathname) {
				if (pathname.isDirectory()) {
					return true;
				}
				return false;
			}
		};
		File[] childFiles = this.folder.listFiles(filtru);
		if (childFiles == null) {
			return;
		}
		for (int i = 0; i < childFiles.length; i++) {
			File childFile = childFiles[i];
			this.childrens.add(new FolderNode(this, childFile));
		}
	}

	@Override
	public Image getImage() {
		return AppImages.getImage16(AppImages.IMG_COLLAPSE);
	}

	@Override
	public Color getForeground() {
		return null;
	}

	@Override
	public Color getBackground() {
		return null;
	}

	@Override
	public Font getFont() {
		return null;
	}

}
