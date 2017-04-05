package com.papao.books.view.providers.tree;

import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ILazyTreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;

import java.util.List;

/**
 * This is the Lazy implementation for treeviewers using SWT.Virtual flag. As in jFace 3.6, this
 * implementation does NOT supports sorting and filtering. Vezi {@link TreeViewer}
 */
public class TreeContentProviderVirtual implements ILazyTreeContentProvider {

	private final TreeViewer viewer;
	private ITreeNode[] elements;

	public TreeContentProviderVirtual(final ColumnViewer viewer) {
		this.viewer = (TreeViewer) viewer;
	}

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

	public boolean hasChildren(final Object element) {
		return ((ITreeNode) element).hasChildrens();
	}

	public Object[] getElements(final Object inputElement) {
		return getChildren(inputElement);
	}

	@Override
	public void dispose() {
	}

	@Override
	public void inputChanged(final Viewer viewer_, final Object oldInput, final Object newInput) {
		if (newInput == null) {
			this.elements = new ITreeNode[0];
			return;
		}
		this.elements = ((ITreeNode) newInput).getChildrens().toArray(new ITreeNode[((ITreeNode) newInput).getChildrens().size()]);
	}

	@Override
	public void updateChildCount(final Object element, final int currentChildCount) {
		int length = 0;
		if (element instanceof ITreeNode) {
			ITreeNode node = (ITreeNode) element;
			length = node.getChildrens().size();
		}
		this.viewer.setChildCount(element, length);
	}

	@Override
	public void updateElement(final Object parent, final int index) {
		if (parent instanceof ITreeNode) {
			List<ITreeNode> list = ((ITreeNode) parent).getChildrens();
			this.viewer.replace(parent, index, list.get(index));
			this.viewer.setChildCount(list.get(index), list.get(index).getChildrens().size());
		} else {
			this.viewer.replace(parent, index, this.elements[index]);
			this.viewer.setChildCount(this.elements[index],
					this.elements[index].getChildrens().size());
		}
	}

}
