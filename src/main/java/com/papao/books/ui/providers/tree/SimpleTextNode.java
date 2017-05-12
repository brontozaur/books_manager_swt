package com.papao.books.ui.providers.tree;

import com.papao.books.ui.util.StringUtil;
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
    private Image image;
    private Font font;
    private Color background;
    private Color foreground;
    private Object key;
    private final List<SimpleTextNode> ancestors;
    private static final String INVISIBLE_ROOT = "invisibleRoot";
    private ITreeNode parent;
    private List<ITreeNode> childrens;
    private int count;
    private Object queryValue;
    private NodeType nodeType;

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
        this.ancestors = new ArrayList<>();
        if (parent != null) {
            parent.getChildrens().add(this);
        }
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
        for (ListIterator<ITreeNode> it = parinte.getChildrens().listIterator(); it.hasNext(); ) {
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

    public static boolean isPassingFilterNume(final SimpleTextNode node,
                                              final String searchedValue) {
        if (!node.hasChildrens()) {
            return StringUtil.compareStrings(searchedValue, node.getName());
        } else if (StringUtil.compareStrings(searchedValue, node.getName())) {
            return true;
        }
        List<ITreeNode> childrens = node.getChildrens();
        for (ITreeNode children : childrens) {
            if (SimpleTextNode.isPassingFilterNume((SimpleTextNode) children, searchedValue)) {
                return true;
            }
        }
        return false;
    }

    public static ViewerFilter[] getFilter(final String searchValue) {
        SimpleTextNode.filter = new ViewerFilter() {
            @Override
            public boolean select(final Viewer viewer,
                                  final Object parentElement,
                                  final Object element) {
                return SimpleTextNode.isPassingFilterNume((SimpleTextNode) element, searchValue);
            }
        };
        SimpleTextNode.filters = new ViewerFilter[]{
                SimpleTextNode.filter};
        return SimpleTextNode.filters;
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
        return " (" + count + ")";
    }

    public void modifyCount(boolean showItemCount,
                            boolean processParents) {
        String numeNod = getName();
        if (showItemCount) {
            if (numeNod.contains(" (")) {
                numeNod = numeNod.substring(0, numeNod.lastIndexOf(" ("));
            }
            numeNod += getItemCountStr();
        }
        if (processParents) {
            List<SimpleTextNode> parents = getAncestors();
            if (parents != null) {
                for (int i = 0; i < parents.size(); i++) {
                    parents.get(i).modifyCount(showItemCount, processParents);
                }
            }
        }
        setName(numeNod);
    }

    public static SimpleTextNode getRecentNode(final Object invisibleRoot) {
        if (invisibleRoot instanceof SimpleTextNode) {
            return SimpleTextNode.getRecentNode((SimpleTextNode) invisibleRoot);
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

    public void increment() {
        count++;
    }

    public void decrement() {
        count--;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getCount() {
        return count;
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

    public boolean isAllNode() {
        return nodeType == NodeType.ALL;
    }

    public Object getQueryValue() {
        return queryValue;
    }

    public void setQueryValue(Object queryValue) {
        this.queryValue = queryValue;
    }

    public NodeType getNodeType() {
        return nodeType;
    }

    public void setNodeType(NodeType nodeType) {
        this.nodeType = nodeType;
    }

    public Date getMinDate() {
        Calendar minDate = Calendar.getInstance();
        minDate.setTime((Date) queryValue);
        minDate.set(Calendar.AM_PM, Calendar.AM);
        minDate.set(Calendar.HOUR, minDate.getMinimum(Calendar.HOUR));
        minDate.set(Calendar.MINUTE, minDate.getMinimum(Calendar.MINUTE));
        minDate.set(Calendar.SECOND, minDate.getMinimum(Calendar.SECOND));
        if (nodeType == NodeType.DAY) {
            return minDate.getTime();
        } else if (nodeType == NodeType.MONTH) {
            minDate.set(Calendar.DAY_OF_MONTH, minDate.getMinimum(Calendar.DAY_OF_MONTH));
            return minDate.getTime();
        } else {
            minDate.set(Calendar.DAY_OF_MONTH, minDate.getMinimum(Calendar.DAY_OF_MONTH));
            minDate.set(Calendar.MONTH, minDate.getMinimum(Calendar.MONTH));
            return minDate.getTime();
        }
    }

    public Date getMaxDate() {
        Calendar maxDate = Calendar.getInstance();
        maxDate.setTime((Date) queryValue);
        maxDate.set(Calendar.AM_PM, Calendar.PM);
        maxDate.set(Calendar.HOUR, maxDate.getMaximum(Calendar.HOUR));
        maxDate.set(Calendar.MINUTE, maxDate.getMaximum(Calendar.MINUTE));
        maxDate.set(Calendar.SECOND, maxDate.getMaximum(Calendar.SECOND));
        if (nodeType == NodeType.DAY) {
            return maxDate.getTime();
        } else if (nodeType == NodeType.MONTH) {
            maxDate.set(Calendar.DAY_OF_MONTH, maxDate.getMaximum(Calendar.DAY_OF_MONTH));
            return maxDate.getTime();
        } else {
            maxDate.set(Calendar.DAY_OF_MONTH, maxDate.getMaximum(Calendar.DAY_OF_MONTH));
            maxDate.set(Calendar.MONTH, maxDate.getMaximum(Calendar.MONTH));
            return maxDate.getTime();
        }
    }
}
