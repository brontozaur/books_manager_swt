package com.papao.books.view.providers.tree;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class TreeContentProvider implements ITreeContentProvider {

	@Override
	public Object[] getChildren(final Object parentElement) {
		if (parentElement instanceof ITreeNode) {
			return ((ITreeNode) parentElement).getChildrens().toArray();
		}
		return new Object[0];
	}

	@Override
	public Object getParent(final Object element) {
		return ((ITreeNode) element).getParent();
	}

	@Override
	public boolean hasChildren(final Object element) {
		return ((ITreeNode) element).hasChildrens();
	}

	@Override
	public Object[] getElements(final Object inputElement) {
		return getChildren(inputElement);
	}

	@Override
	public void dispose() {}

	@Override
	public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
		getElements(newInput);
	}

	/**
	 * @param element
	 */
	public static boolean isChecked(final Object element) {
		return false;
	}
}
