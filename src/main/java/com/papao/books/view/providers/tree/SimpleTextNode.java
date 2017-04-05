package com.papao.books.view.providers.tree;

import com.papao.books.model.AbstractDB;
import com.papao.books.view.util.StringUtil;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;

import java.util.*;

public class SimpleTextNode implements ITreeNode {

	private String node;
	private String[] nodes = new String[0];
	private static ViewerFilter filter;
	private static ViewerFilter[] filters;
	private Map<String, AbstractDB> dbElements = new TreeMap<>();
	private Image image;
	private Font font;
	private Color background;
	private Color foreground;
	private Object key;
	private final List<SimpleTextNode> ancestors;
	private static final String INVISIBLE_ROOT = "invisibleRoot";
	public static final String RECENT_NODE = " >> Operatii recente";
	private AbstractDB dbElement;
	private ITreeNode parent;
	private List<ITreeNode> childrens;

	public SimpleTextNode(final String node) {
		this(null, node);
	}

	public SimpleTextNode(final ITreeNode parent, final String node) {
		setParent(parent);
		if (node == null) {
			this.node = SimpleTextNode.INVISIBLE_ROOT;
		} else {
			this.node = node;
		}
		setKey(this.node);
		this.ancestors = new ArrayList<SimpleTextNode>();
	}

	@Override
	public String getName() {
		return this.node;
	}

	public void setNodes(final String[] nodes) {
		this.nodes = nodes;
		createChildrens();
	}

	public void add(final SimpleTextNode newNode) {
		SimpleTextNode.add(this, newNode);
	}

	public static void add(final SimpleTextNode parinte, final SimpleTextNode newNode) {
		newNode.setParent(parinte);
		parinte.getChildrens().add(newNode);
	}

	public SimpleTextNode getChildren(final String nodeKey) {
		return SimpleTextNode.getChildren(this, nodeKey);
	}

	public static SimpleTextNode getChildren(final ITreeNode parinte, final String nodeKey) {
		for (ListIterator<ITreeNode> it = parinte.getChildrens().listIterator(); it.hasNext();) {
			SimpleTextNode tmp = (SimpleTextNode) it.next();
			if (tmp.getKey().equals(nodeKey)) {
				return tmp;
			}
		}
		return null;
	}

	@Override
	public boolean hasChildrens() {
		return getChildrens().size() > 0;
	}

	public final static boolean isPassingFilterNume(final SimpleTextNode node,
													final String searchedValue) {
		if (!node.hasChildrens()) {
			return StringUtil.compareStrings(searchedValue, node.getName(), false);
		} else if (StringUtil.compareStrings(searchedValue, node.getName(), false)) {
			return true;
		}
		List<ITreeNode> childrens = node.getChildrens();
		for (Iterator<ITreeNode> it = childrens.iterator(); it.hasNext();) {
			if (SimpleTextNode.isPassingFilterNume((SimpleTextNode) it.next(), searchedValue)) {
				return true;
			}
		}
		return false;
	}

	public static ViewerFilter[] getFilter(final String searchValue) {
		SimpleTextNode.filter = new ViewerFilter() {
			@Override
			public boolean select(	final Viewer viewer,
									final Object parentElement,
									final Object element) {
				return SimpleTextNode.isPassingFilterNume((SimpleTextNode) element, searchValue);
			}
		};
		SimpleTextNode.filters = new ViewerFilter[] {
			SimpleTextNode.filter };
		return SimpleTextNode.filters;
	}

	public final Map<String, AbstractDB> getDbElements() {
		return this.dbElements;
	}

	public final void setDbElements(final Map<String, AbstractDB> dbElements) {
		this.dbElements = dbElements;
	}

	@Override
	public final Image getImage() {
		return this.image;
	}

	public final void setImage(final Image image) {
		this.image = image;
	}

	@Override
	public final Font getFont() {
		return this.font;
	}

	public final void setFont(final Font font) {
		this.font = font;
	}

	@Override
	public final Color getBackground() {
		return this.background;
	}

	public final void setBackground(final Color background) {
		this.background = background;
	}

	@Override
	public final Color getForeground() {
		return this.foreground;
	}

	public final void setForeground(final Color foreground) {
		this.foreground = foreground;
	}

	public final Object getKey() {
		if (this.key == null) {
			return getName() != null ? getName() : "";
		}
		return this.key;
	}

	public AbstractDB getDbElement() {
		return this.dbElement;
	}

	public void setDbElement(final AbstractDB dbElement) {
		this.dbElement = dbElement;
	}

	public final void setKey(final Object key) {
		this.key = key;
	}

	public final void setName(final String newName) {
		this.node = newName;
	}

	public final SimpleTextNode getInvisibleRoot() {
		return SimpleTextNode.getInvisibleRoot(this);
	}

	public final static SimpleTextNode getInvisibleRoot(final SimpleTextNode someChild) {
		SimpleTextNode parentNode = (SimpleTextNode) someChild.getParent();
		do {
			if ((parentNode != null) && (parentNode.getParent() != null)) {
				parentNode = (SimpleTextNode) parentNode.getParent();
			}
		}
		while ((parentNode != null) && (parentNode.getParent() != null));
		return parentNode;
	}

	public List<SimpleTextNode> getAncestors() {
		return getAncestors(this);
	}

	public List<SimpleTextNode> getAncestors(final SimpleTextNode someNode) {
		SimpleTextNode parentNode = (SimpleTextNode) someNode.getParent();
		this.ancestors.clear();
		do {
			if ((parentNode != null) && (parentNode.getParent() != null)) {
				this.ancestors.add(parentNode);
				parentNode = (SimpleTextNode) parentNode.getParent();
			}
		}
		while ((parentNode != null) && (parentNode.getParent() != null));
		return this.ancestors;
	}

	public static SimpleTextNode getLastChildOnLastParent(final SimpleTextNode nodeStart) {
		SimpleTextNode root = SimpleTextNode.getInvisibleRoot(nodeStart);
		if (root == null) {
			return null;
		}
		SimpleTextNode result = SimpleTextNode.getLastChildOfParent(root);
		while (SimpleTextNode.getLastChildOfParent(result) != null) {
			result = SimpleTextNode.getLastChildOfParent(result);
		}
		return result;
	}

	public static SimpleTextNode getLastChildOfParent(final SimpleTextNode parinte) {
		if (parinte == null) {
			return null;
		}
		List<ITreeNode> copii = parinte.getChildrens();
		if ((copii == null) || copii.isEmpty()) {
			return null;
		}
		return (SimpleTextNode) copii.get(copii.size() - 1);
	}

	public String getItemCountStr() {
		if (getDbElements() == null) {
			return "(0)";
		}
		return "(" + getDbElements().size() + ")";
	}

	public static void decrementByOne(	final SimpleTextNode node,
										final AbstractDB element,
										final boolean showItemCount,
										final boolean processParents) {
		if (node == null) {
			return;
		}
		node.getDbElements().remove(element.getId());
		String numeNod = node.getName();
		if (showItemCount) {
			if (numeNod.indexOf('(') != -1) {
				numeNod = numeNod.substring(0, numeNod.lastIndexOf('('));
			}
			numeNod += node.getItemCountStr();
		}
		if (processParents) {
			List<SimpleTextNode> parents = node.getAncestors();
			if (parents != null) {
				for (int i = 0; i < parents.size(); i++) {
					SimpleTextNode.decrementByOne(parents.get(i), element, showItemCount, true);
				}
			}
		}
		List<ITreeNode> copii = node.getChildrens();
		if (copii != null) {
			for (int i = 0; i < copii.size(); i++) {
				SimpleTextNode.decrementByOne((SimpleTextNode) copii.get(i),
						element,
						showItemCount,
						false);
			}
		}
		node.setName(numeNod);
	}

	public static SimpleTextNode getRecentNode(final Object invisibleRoot) {
		if (invisibleRoot instanceof SimpleTextNode) {
			return SimpleTextNode.getRecentNode((SimpleTextNode) invisibleRoot);
		}
		return null;
	}

	public static SimpleTextNode getRecentNode(final SimpleTextNode invisibleRoot) {
		List<ITreeNode> childrens = invisibleRoot.getChildrens();
		for (ITreeNode child : childrens) {
			String name = ((SimpleTextNode) child).getName();
			if (name.indexOf('(') != -1) {
				name = name.substring(0, name.indexOf('('));
			}
			if (name.equals(SimpleTextNode.RECENT_NODE)) {
				return (SimpleTextNode) child;
			}
		}
		return null;
	}
//
//	public static void addViewerElement(final AbstractBoneUnifiedLV2 bone,
//										final AbstractDB adb,
//										final boolean showItemCount) throws Exception {
//		if (!(bone instanceof ITableBone)) {
//			return;
//		}
//		final TreeViewer treeViewer = bone.getLeftTreeViewer();
//		final ColumnViewer columnViewer = ((ITableBone) bone).getViewer();
//		if ((treeViewer == null) || treeViewer.getControl().isDisposed()) {
//			return;
//		}
//		if (!(treeViewer.getInput() instanceof SimpleTextNode)) {
//			treeViewer.setInput(new SimpleTextNode(null));
//		}
//		((SimpleTextNode) treeViewer.getInput()).getDbElements().put(adb.getId(), adb);
//		if (FiltruAplicatie.isLeftTreeShowRecentActivity()) {
//			SimpleTextNode recentNode = SimpleTextNode.getRecentNode(treeViewer.getInput());
//			if (recentNode == null) {
//				recentNode = new SimpleTextNode(
//					(SimpleTextNode) treeViewer.getInput(),
//					SimpleTextNode.RECENT_NODE);
//				recentNode.setDbElements(new HashMap<Long, AbstractDB>());
//				if (showItemCount) {
//					recentNode.setName(SimpleTextNode.RECENT_NODE + recentNode.getItemCountStr());
//				}
//				recentNode.setImage(AppImages.getImage16(AppImages.IMG_INFO));
//				((SimpleTextNode) treeViewer.getInput()).add(recentNode);
//			}
//			recentNode.getDbElements().put(adb.getId(), adb);
//			if (showItemCount) {
//				recentNode.setName(SimpleTextNode.RECENT_NODE + recentNode.getItemCountStr());
//			}
//			ISelection sel = treeViewer.getSelection();
//			if ((sel instanceof TreeSelection)
//					&& recentNode.equals(((TreeSelection) sel).getFirstElement())) {
//				columnViewer.setInput(((SimpleTextNode) ((TreeSelection) sel).getFirstElement()).getDbElements());
//			}
//			treeViewer.refresh();
//		} else {
//			bone.populateLeftTree(false);
//		}
//	}
//
//	public static void modifyViewerElement(	final AbstractBoneUnifiedLV2 bone,
//											final AbstractDB oldAdb,
//											final AbstractDB newAdb,
//											final boolean showItemCount) throws Exception {
//		SimpleTextNode.removeViewerElement(bone, oldAdb, showItemCount, true);
//		SimpleTextNode.addViewerElement(bone, newAdb, showItemCount);
//	}
//
//	public static void removeViewerElement(	final AbstractBoneUnifiedLV2 bone,
//											final AbstractDB element,
//											final boolean showNumbers) {
//		SimpleTextNode.removeViewerElement(bone, element, showNumbers, true);
//	}
//
//	/**
//	 * Metoda face remove in mod recursiv din map-ul de elemente al fiecarui nod implicat, si
//	 * actualizeaza afisarea in interfata (ex - s-a selectat luna, si s-a sters un element : se va
//	 * decrementa si din map-ul anului si al zilei, dupa cheia elementului, etc).
//	 */
//	public static void removeViewerElement(	final AbstractBoneUnifiedLV2 bone,
//											final AbstractDB element,
//											final boolean showNumbers,
//											final boolean recalculTotaluri) {
//		ITreeSelection leftSelection = null;
//		if (!(bone instanceof ITableBone)) {
//			return;
//		}
//		try {
//			final ColumnViewer viewer = ((ITableBone) bone).getViewer();
//			final TreeViewer treeViewer = bone.getLeftTreeViewer();
//			if ((viewer == null) || !(viewer.getContentProvider() instanceof AdbContentProvider)) {
//				return;
//			}
//			AbstractDB[] elements = ((AdbContentProvider) viewer.getContentProvider()).getElements();
//			for (int i = 0; i < elements.length; i++) {
//				if (element.getId() == elements[i].getId()) {
//					AbstractDB[] temp = new AbstractDB[elements.length - 1];
//					System.arraycopy(elements, 0, temp, 0, i);
//					System.arraycopy(elements, i + 1, temp, i, temp.length - i);
//					elements = temp;
//					viewer.setInput(elements);
//					if (treeViewer != null) {
//						leftSelection = (ITreeSelection) treeViewer.getSelection();
//						if (treeViewer.getInput() instanceof SimpleTextNode) {
//							((SimpleTextNode) treeViewer.getInput()).getDbElements().remove(element.getId());
//						}
//						if (leftSelection != null) {
//							Object obj = leftSelection.getFirstElement();
//							if (obj instanceof SimpleTextNode) {
//								SimpleTextNode.decrementByOne((SimpleTextNode) obj,
//										element,
//										showNumbers,
//										showNumbers);
//							}
//						}
//						treeViewer.refresh();
//					}
//				}
//			}
//		}
//		finally {
//			if (recalculTotaluri) {
//				bone.computeTotal();
//			}
//		}
//	}

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
		if (this.nodes != null) {
			for (String tmp : this.nodes) {
				add(new SimpleTextNode(this, tmp));
			}
		}
	}

}
