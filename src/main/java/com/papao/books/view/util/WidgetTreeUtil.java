package com.papao.books.view.util;

import com.papao.books.view.AppImages;
import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.TreeAdapter;
import org.eclipse.swt.events.TreeEvent;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class WidgetTreeUtil {

	private static Logger logger = Logger.getLogger(WidgetTreeUtil.class);

	public static final int ADD_RESIZE = 1 << 1;
	public static final int ADD_ORDER = 1 << 2;
	public static final int ADD_HEADER = 1 << 3;

	private WidgetTreeUtil() {}

	/**
	 * La evenimentul de selectie se deseneaza un gradient pe itemul selectat, pe toata lungimea
	 * tabelei, format din c1(stanga) + c2(dreapta)
	 * 
	 * @param tree
	 *            tabela parama
	 * @param c1
	 *            culoarea din stanga
	 * @param c2
	 *            culoarea din dreapta
	 */
	public static void addCustomGradientSelectionListenerToTree(final Tree tree,
																final Color c1,
																final Color c2) {
		try {
			if ((tree == null) || tree.isDisposed()) {
				return;
			}
			tree.addListener(SWT.EraseItem, new Listener() {

				@Override
				public void handleEvent(final Event event) {
					event.detail &= ~SWT.HOT;
					if ((event.detail & SWT.SELECTED) != 0) {
						GC gc = event.gc;
						Rectangle area = tree.getClientArea();
						/*
						 * If you wish to paint the selection beyond the end of last column, you
						 * must change the clipping region.
						 */
						int columnCount = tree.getColumnCount();
						if ((event.index == (columnCount - 1)) || (columnCount == 0)) {
							int width = (area.x + area.width) - event.x;
							if (width > 0) {
								Region region = new Region();
								gc.getClipping(region);
								region.add(event.x, event.y, width, event.height);
								gc.setClipping(region);
								region.dispose();
							}
						}
						gc.setAdvanced(true);
						if (gc.getAdvanced()) {
							gc.setAlpha(255);
						}
						Rectangle rect = event.getBounds();
						Color foreground = gc.getForeground();
						Color background = gc.getBackground();
						gc.setForeground(((c1 == null) || c1.isDisposed())	? ColorUtil.COLOR_ROSU_SEMI_ROSU
																			: c1);
						gc.setBackground(((c2 == null) || c2.isDisposed())	? ColorUtil.COLOR_ROSU_SEMI_ROSU2
																			: c2);
						int width = 0;
						for (TreeColumn col : tree.getColumns()) {
							width += col.getWidth();
						}
						gc.fillGradientRectangle(0,
								rect.y,
								width > 0 ? width : tree.getClientArea().width,
								rect.height,
								false);
						// restore colors for subsequent drawing
						gc.setForeground(foreground);
						gc.setBackground(background);
						event.detail &= ~SWT.SELECTED;
					}
				}
			});
		}
		catch (Exception exc) {
			logger.error(exc.getMessage(), exc);
		}
	}

	/**
	 * @param tree
	 *            tree-ul care va fi "scanat"
	 * @return itemii tree-ului, recursiv, pana la frunze, indiferent de structura lui, sau null
	 *         daca tree-ul nu are nici un copil.
	 */
	@SuppressWarnings("boxing")
	public static TreeItem[] getTreeItemsX(final Tree tree) {
		TreeItem[] result = null, items = null;
		Map<Integer, TreeItem> worker = new HashMap<Integer, TreeItem>();
		try {
			if ((tree == null) || tree.isDisposed()) {
				return null;
			}
			items = tree.getItems();
			if (items.length == 0) {
				return null;
			}
			for (int i = 0; i < items.length; i++) {
				Map<Integer, TreeItem> tmp = WidgetTreeUtil.getTreeItemsY(items[i]);
				for (TreeItem temp : tmp.values()) {
					worker.put(temp.hashCode(), temp);
				}
			}
			if (!worker.isEmpty()) {
				result = new TreeItem[worker.size()];
				int i = 0;
				for (TreeItem it : worker.values()) {
					result[i++] = it;
				}
			}
		}
		catch (Exception exc) {
			logger.error(exc.getMessage(), exc);
			return null;
		}
		return result;
	}

	/**
	 * @param parent
	 *            un TreeItem de referinta
	 * @return recursiv, toti itemii care il au pe parent ca parinte + parent
	 */
	@SuppressWarnings("boxing")
	public static Map<Integer, TreeItem> getTreeItemsY(final TreeItem parent) {
		Map<Integer, TreeItem> worker = new HashMap<Integer, TreeItem>();
		try {
			if ((parent == null) || parent.isDisposed()) {
				return worker;
			}
			if (worker.get(parent.hashCode()) == null) {
				worker.put(parent.hashCode(), parent);
			}
			if (parent.getItemCount() == 0) {
				return worker;
			}
			TreeItem[] items = parent.getItems();
			for (int i = 0; i < items.length; i++) {
				if (worker.get(items[i].hashCode()) == null) {
					worker.put(items[i].hashCode(), items[i]);
				}
				if (items[i].getItemCount() > 0) {
					Map<Integer, TreeItem> tmp = WidgetTreeUtil.getTreeItemsY(items[i]);
					for (TreeItem temp : tmp.values()) {
						worker.put(temp.hashCode(), temp);
					}
				}
			}
		}
		catch (Exception exc) {
			logger.error(exc.getMessage(), exc);
			return null;
		}
		return worker;
	}

	public static void expandTreeItems(final Tree tree, final boolean expand) {
		try {
			if ((tree == null) || tree.isDisposed()) {
				return;
			}
			final TreeItem[] items = WidgetTreeUtil.getTreeItemsX(tree);
			if ((items == null) || (items.length == 0)) {
				return;
			}
			for (TreeItem it : items) {
				if ((it != null) && !it.isDisposed()) {
					it.setExpanded(expand);
					if (it.getItemCount() > 0) {
						if (it.getExpanded()) {
							it.setImage(AppImages.getImage16(AppImages.IMG_EXPAND));
						} else {
							it.setImage(AppImages.getImage16(AppImages.IMG_COLLAPSE));
						}
					}
				}
			}
		}
		catch (Exception exc) {
			logger.error(exc.getMessage(), exc);
		}
		finally {
			if ((tree != null) && !tree.isDisposed()) {
				tree.setVisible(true);
			}
		}
	}

	public static void addTreeExpandMenu(final Tree tree) {
		Menu menu = null;
		MenuItem subItem;
		try {
			if ((tree == null) || tree.isDisposed()) {
				return;
			}
			menu = new Menu(tree);

			subItem = new MenuItem(menu, SWT.NONE);
			subItem.setText("Expandare");
			subItem.setImage(AppImages.getImage16(AppImages.IMG_EXPAND));
			subItem.addListener(SWT.Selection, new Listener() {
				@Override
				public void handleEvent(final Event e) {
					WidgetTreeUtil.expandTreeItems(tree, true);
				}
			});

			new MenuItem(menu, SWT.SEPARATOR);

			subItem = new MenuItem(menu, SWT.NONE);
			subItem.setText("Colapsare");
			subItem.setImage(AppImages.getImage16(AppImages.IMG_COLLAPSE));
			subItem.addListener(SWT.Selection, new Listener() {
				@Override
				public void handleEvent(final Event e) {
					WidgetTreeUtil.expandTreeItems(tree, false);
				}
			});
		}
		catch (Exception exc) {
			logger.error(exc.getMessage(), exc);
		}
	}

	public static void customizeTreeBehaviour(final Tree tree) {
		if ((tree == null) || tree.isDisposed()) {
			return;
		}
		tree.addListener(SWT.Selection, new Listener() {
			@Override
			public final void handleEvent(final Event e) {
				TreeItem item = null;
				try {
					if (tree.getSelectionCount() <= 0) {
						return;
					}
					item = tree.getSelection()[0];
					if ((item != null) && !item.isDisposed() && (item.getItemCount() > 0)) {
						item.setExpanded(true);
						if (item.getExpanded()) {
							item.setImage(AppImages.getImage16(AppImages.IMG_EXPAND));
						} else {
							item.setImage(AppImages.getImage16(AppImages.IMG_COLLAPSE));
						}
					}
				}
				catch (Exception exc) {
					logger.error(exc.getMessage(), exc);
				}
			}
		});
		tree.addTreeListener(new TreeAdapter() {
			@Override
			public void treeExpanded(final TreeEvent event) {
				TreeItem item = null;
				try {
					item = (TreeItem) event.item;
					item.setImage(AppImages.getImage16(AppImages.IMG_EXPAND));
				}
				catch (Exception exc) {
					logger.error(exc.getMessage(), exc);
				}
			}

			@Override
			public void treeCollapsed(final TreeEvent event) {
				TreeItem item = null;
				try {
					item = (TreeItem) event.item;
					item.setImage(AppImages.getImage16(AppImages.IMG_COLLAPSE));
				}
				catch (Exception exc) {
					logger.error(exc.getMessage(), exc);
				}
			}
		});
	}

	public static void checkAll(final Tree tree, final boolean checked) {
		try {
			if ((tree == null) || tree.isDisposed() || ((tree.getStyle() & SWT.CHECK) == 0)) {
				return;
			}
			TreeItem[] items = WidgetTreeUtil.getTreeItemsX(tree);
			if (items == null) {
				return;
			}
			final int LENGTH = items.length;
			for (int i = 0; i < LENGTH; i++) {
				items[i].setChecked(checked);
				if (checked && (items[i].getItemCount() > 0) && !items[i].getExpanded()) {
					items[i].setExpanded(true);
				}
			}
		}
		catch (Exception exc) {
			logger.error(exc.getMessage(), exc);
		}
	}

	public static TreeItem[] getCheckedItems(final Tree tree) {
		TreeItem[] checkedItems = null;
		List<TreeItem> vecChecked = new ArrayList<TreeItem>();
		try {
			if ((tree == null) || tree.isDisposed() || ((tree.getStyle() & SWT.CHECK) == 0)) {
				return null;
			}
			TreeItem[] items = WidgetTreeUtil.getTreeItemsX(tree);
			if (items == null) {
				return null;
			}
			final int LENGTH = items.length;
			for (int i = 0; i < LENGTH; i++) {
				if (items[i].getChecked()) {
					vecChecked.add(items[i]);
				}
			}
			if (!vecChecked.isEmpty()) {
				checkedItems = new TreeItem[vecChecked.size()];
				for (int i = 0; i < vecChecked.size(); i++) {
					checkedItems[i] = vecChecked.get(i);
				}
			}
		}
		catch (Exception exc) {
			logger.error(exc.getMessage(), exc);
			return null;
		}
		return checkedItems;
	}

	public static String[] getColumnsNames(final Tree tree) {
		String[] result = null;
		try {
			if ((tree == null) || tree.isDisposed()) {
				return result;
			}
			result = new String[tree.getColumnCount()];
			for (int i = 0; i < tree.getColumnOrder().length; i++) {
				result[i] = tree.getColumn(tree.getColumnOrder()[i]).getText();
			}
		}
		catch (Exception exc) {
			logger.error(exc.getMessage(), exc);
			return null;
		}
		return result;
	}

	public static int[] getColumnsAligns(final Tree tree) {
		int[] result = null;
		try {
			if ((tree == null) || tree.isDisposed()) {
				return result;
			}
			result = new int[tree.getColumnCount()];
			int i = 0;
			TreeColumn[] cols = tree.getColumns();
			for (TreeColumn col : cols) {
				result[i++] = col.getAlignment();
			}
		}
		catch (Exception exc) {
			logger.error(exc.getMessage(), exc);
			return null;
		}
		return result;
	}

	private static void addTreeColumnsResizeObserver(	final Tree tree,
														final Class<?> clazz,
														final String sufix2) {
		if ((tree == null) || tree.isDisposed() || (tree.getColumnCount() == 0) || (clazz == null)) {
			return;
		}
		final TreeColumn[] cols = tree.getColumns();
		for (final TreeColumn col : cols) {
			col.addListener(SWT.Resize, new Listener() {
				@Override
				public final void handleEvent(final Event e) {
//					int[] dims = FileFilterUtil.getSavedGridDims(cols.length, clazz, sufix2);
//					if (col.getWidth() > 0) {
//						dims[tree.indexOf(col)] = col.getWidth();
//						FilterUtil.saveDims(dims, clazz, sufix2);
//					}
//					col.setResizable(col.getWidth() > 0);
				}
			});
		}
	}

	private static Menu createTreeHeaderMenu(final Tree tree,
                                             final Class<?> clazz,
                                             final Menu tableMenu,
                                             final String sufix2) {
		if ((tree == null) || tree.isDisposed() || (clazz == null)) {
			return null;
		}
		final Menu headerMenu = new Menu(tree.getShell(), SWT.POP_UP);
		headerMenu.setData("header");
		try {
			String[] colNames = WidgetTreeUtil.getColumnsNames(tree);
//			headerMenu.addListener(SWT.Show, new Listener() {
//				@Override
//				public final void handleEvent(final Event e) {
//					final boolean[] visibleCols = FilterUtil.getSavedVisibleCols(tree.getColumnCount(),
//							clazz,
//							sufix2);
//					for (int i = 2; i < headerMenu.getItemCount(); i++) {
//						headerMenu.getItem(i).setSelection(visibleCols[tree.getColumnOrder()[i - 2]]);
//					}
//				}
//			});

			final MenuItem itemAdvanced = new MenuItem(headerMenu, SWT.PUSH);
			itemAdvanced.setText("Selectie avansata");

			new MenuItem(headerMenu, SWT.SEPARATOR);

//			for (int i = 0; i < colNames.length; i++) {
//				final MenuItem item = new MenuItem(headerMenu, SWT.CHECK);
//				item.setText(colNames[i]);
//				item.addListener(SWT.Selection, new Listener() {
//					@Override
//					public final void handleEvent(final Event e) {
//						final int[] savedDims = FilterUtil.getSavedGridDims(tree.getColumnCount(),
//								clazz,
//								sufix2);
//						boolean[] visible = FilterUtil.getSavedVisibleCols(tree.getColumnCount(),
//								clazz,
//								sufix2);
//						final int itemIndex = headerMenu.indexOf(item) - 2;
//						if (item.getSelection()) {
//							tree.getColumn(tree.getColumnOrder()[itemIndex]).setWidth(savedDims[tree.getColumnOrder()[itemIndex]]);
//							visible[tree.getColumnOrder()[itemIndex]] = true;
//							tree.getColumn(tree.getColumnOrder()[itemIndex]).setResizable(true);
//							tree.getColumn(tree.getColumnOrder()[itemIndex]).setMoveable(true);
//						} else {
//							tree.getColumn(tree.getColumnOrder()[itemIndex]).setWidth(0);
//							visible[tree.getColumnOrder()[itemIndex]] = false;
//							tree.getColumn(tree.getColumnOrder()[itemIndex]).setResizable(false);
//							tree.getColumn(tree.getColumnOrder()[itemIndex]).setMoveable(false);
//						}
//						FilterUtil.saveVisibleCols(visible, clazz, sufix2);
//					}
//				});
//			}

			tree.addListener(SWT.Dispose, new Listener() {
				@Override
				public void handleEvent(final Event event) {
					headerMenu.dispose();
					if (tableMenu != null) {
						tableMenu.dispose();
					}
				}
			});
		}
		catch (Exception exc) {
//			SQLLibrary.processErr(exc, logger);
		}
		return headerMenu;
	}

	private static void addTreeColumnsOrderListener(final Tree tree,
													final Class<?> clazz,
													final String sufix2) {
		if ((tree == null) || tree.isDisposed() || (tree.getColumnCount() == 0) || (clazz == null)) {
			return;
		}
		final TreeColumn[] cols = tree.getColumns();
//		for (TreeColumn col : cols) {
//			col.addListener(SWT.Move, new Listener() {
//				@Override
//				public final void handleEvent(final Event e) {
//					FilterUtil.saveOrder(tree.getColumnOrder(), clazz, sufix2);
//				}
//			});
//		}
	}

	/**
	 * @param tree
	 *            some real non-null, non-disposed tree. If null or disposed, method will return
	 *            quietely
	 * @param clazz
	 *            some class
	 */
	public static void customizeTree(final Tree tree, final Class<?> clazz) {
		WidgetTreeUtil.customizeTree(tree, clazz, null);
	}

	/**
	 * @param tree
	 *            some real non-null, non-disposed tree. If null or disposed, method will return
	 *            quietely
	 * @param clazz
	 *            some class
	 * @param sufix2
	 *            a string delimiter to create unique que on the specified class. May be ignored if
	 *            class has a single grid The method calls
	 */
	public static void customizeTree(final Tree tree, final Class<?> clazz, final String sufix2) {
		WidgetTreeUtil.customizeTree(tree, clazz, sufix2, WidgetTreeUtil.ADD_RESIZE
				| WidgetTreeUtil.ADD_HEADER | WidgetTreeUtil.ADD_ORDER);
	}

	/**
	 * @param tree
	 *            some real non-null, non-disposed tree. If null or disposed, method will return
	 *            quietely
	 * @param clazz
	 *            some class
	 * @param sufix2
	 *            a string delimiter to create unique que on the specified class. May be ignored if
	 *            class has a single grid
	 * @param flags
	 *            at least one options, or this method will be a dummy call
	 */
	public static void customizeTree(	final Tree tree,
										final Class<?> clazz,
										final String sufix2,
										final int flags) {
		if ((tree == null) || tree.isDisposed() || (clazz == null)) {
			return;
		}
		if ((flags & WidgetTreeUtil.ADD_RESIZE) == WidgetTreeUtil.ADD_RESIZE) {
			WidgetTreeUtil.addTreeColumnsResizeObserver(tree, clazz, sufix2);
		}
		if ((flags & WidgetTreeUtil.ADD_HEADER) == WidgetTreeUtil.ADD_HEADER) {
			tree.addListener(SWT.MenuDetect, new Listener() {
				@Override
				public void handleEvent(final Event event) {
					Point pt = tree.getDisplay().map(null, tree, new Point(event.x, event.y));
					Rectangle clientArea = tree.getClientArea();
					boolean header = (clientArea.y <= pt.y)
							&& (pt.y < (clientArea.y + tree.getHeaderHeight()));
					Menu headerMenu = (Menu) tree.getData("header");
					Menu currentMenu = tree.getMenu();
					if ((currentMenu != null) && !currentMenu.isDisposed()) {
						if (!currentMenu.equals(headerMenu)) {
							tree.setData("menu", currentMenu);
						}
					}
					if (header) {
						if ((tree.getMenu() != null) && "header".equals(tree.getMenu().getData())) {
							tree.getMenu().dispose();
						}
						headerMenu = WidgetTreeUtil.createTreeHeaderMenu(tree,
								clazz,
								tree.getMenu(),
								sufix2);
						tree.setData("header", headerMenu);
					}
					if (header) {
						tree.setMenu(headerMenu);
					} else {
						Menu menu = (Menu) tree.getData("menu");
						if ((menu != null) && !menu.isDisposed()) {
							if (menu.equals(headerMenu)) {
								tree.setMenu(null);
							} else {
								tree.setMenu(menu);
							}
						} else if ((tree.getMenu() != null) && !tree.getMenu().isDisposed()) {
							if (tree.getMenu().equals(headerMenu)) {
								tree.setMenu(null);
							} else {
								tree.setData("menu", tree.getMenu());
							}
						}
					}
				}
			});
		}
		if ((flags & WidgetTreeUtil.ADD_ORDER) == WidgetTreeUtil.ADD_ORDER) {
			WidgetTreeUtil.addTreeColumnsOrderListener(tree, clazz, sufix2);
		}
	}
}
