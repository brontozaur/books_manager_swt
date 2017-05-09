package com.papao.books.ui.util;

import com.papao.books.controller.SettingsController;
import com.papao.books.model.config.TableSetting;
import com.papao.books.ui.view.ColumnsChooserView;
import com.papao.books.ui.view.SWTeXtension;
import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.*;

import java.util.ArrayList;
import java.util.List;

public final class WidgetTableUtil {

    private static Logger logger = Logger.getLogger(WidgetTableUtil.class);

    public static final int ADD_RESIZE = 1 << 1;
    public static final int ADD_ORDER = 1 << 2;
    public static final int ADD_HEADER = 1 << 3;

    private WidgetTableUtil() {
    }

    public static void addXRenderListener(final Table table, final Color c1, final Color c2) {
        try {
            if ((table == null) || table.isDisposed()) {
                return;
            }
            table.setBackgroundMode(SWT.INHERIT_FORCE);
            table.addListener(SWT.Resize, new Listener() {

                @Override
                public void handleEvent(final Event e) {
                    Display display = table.getDisplay();
                    Rectangle rect = table.getClientArea();
                    if ((rect.width <= 0) || (rect.height <= 0)) {
                        return;
                    }
                    Image imageGradient = new Image(display, rect.width, rect.height);
                    GC gc = new GC(imageGradient);
                    try {
                        gc.setForeground(c1);
                        gc.setBackground(c2);
                        gc.fillGradientRectangle(rect.x, rect.y, rect.width, rect.height, true);
                    } finally {
                        gc.dispose();
                    }
                    if ((table.getBackgroundImage() != null) && !table.getBackgroundImage().isDisposed()) {
                        table.getBackgroundImage().dispose();
                    }
                    table.setBackgroundImage(imageGradient);

                }
            });

        } catch (Exception exc) {
            logger.error(exc.getMessage(), exc);
        }
    }

    /**
     * La evenimentul de selectie se deseneaza un gradient pe itemul selectat, pe toata lungimea
     * tabelei, format din c1(stanga) + c2(dreapta)
     *
     * @param table tabela parama
     * @param c1    culoarea din stanga
     * @param c2    culoarea din dreapta
     */
    public static void addCustomGradientSelectionListenerToTable(final Table table, final Color c1, final Color c2) {
        try {
            if ((table == null) || table.isDisposed()) {
                return;
            }
            table.addListener(SWT.EraseItem, new Listener() {

                @Override
                public void handleEvent(final Event event) {
                    event.detail &= ~SWT.HOT;
                    if ((event.detail & SWT.SELECTED) != 0) {
                        GC gc = event.gc;
                        Rectangle area = table.getClientArea();
                        /*
                         * If you wish to paint the selection beyond the end of last column, you
                         * must change the clipping region.
                         */
                        int columnCount = table.getColumnCount();
                        if ((event.index == columnCount - 1) || (columnCount == 0)) {
                            int width = area.x + area.width - event.x;
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
                        gc.setForeground(((c1 == null) || c1.isDisposed()) ? ColorUtil.COLOR_ROSU_SEMI_ROSU : c1);
                        gc.setBackground(((c2 == null) || c2.isDisposed()) ? ColorUtil.COLOR_ROSU_SEMI_ROSU2 : c2);
                        gc.fillGradientRectangle(0, rect.y, table.getClientArea().width, rect.height, false);
                        // restore colors for subsequent drawing
                        gc.setForeground(foreground);
                        gc.setBackground(background);
                        event.detail &= ~SWT.SELECTED;
                    }
                }
            });
        } catch (Exception exc) {
            logger.error(exc.getMessage(), exc);
        }
    }

    public static void addTableCustomItemBackground(final Table table, final Color c1, final Color c2) {
        int idx = 0;
        try {
            if ((table == null) || table.isDisposed()) {
                return;
            }
            TableItem[] items = table.getItems();
            int length = items.length;
            for (int i = 0; i < length; i++) {
                if (idx++ % 2 == 0) {
                    items[i].setBackground(((c1 == null) || c1.isDisposed()) ? ColorUtil.COLOR_LABEL_USER : c1);
                } else {
                    items[i].setBackground(((c2 == null) || c2.isDisposed()) ? ColorUtil.COLOR_ALBASTRU_DESCHIS : c1);
                }
            }
        } catch (Exception exc) {
            logger.error(exc.getMessage(), exc);
        }
    }

    public static List<TableItem> getCheckedItems(final Table table) {
        List<TableItem> result = new ArrayList<TableItem>();
        TableItem[] items = null;
        try {
            if ((table == null) || table.isDisposed()) {
                return result;
            }
            items = table.getItems();
            if ((items == null) || (items.length == 0)) {
                return result;
            }
            for (TableItem it : items) {
                if (it.getChecked()) {
                    result.add(it);
                }
            }
        } catch (Exception exc) {
            logger.error(exc.getMessage(), exc);
            result.clear();
        }
        return result;
    }

    public static String[] getColumnsNames(final Table table) {
        String[] result = null;
        try {
            if ((table == null) || table.isDisposed()) {
                return result;
            }
            result = new String[table.getColumnCount()];
            for (int i = 0; i < table.getColumnOrder().length; i++) {
                result[i] = table.getColumn(table.getColumnOrder()[i]).getText();
            }
        } catch (Exception exc) {
            logger.error(exc.getMessage(), exc);
            return null;
        }
        return result;
    }

    public static int[] getColumnsAligns(final Table table) {
        int[] result = null;
        try {
            if ((table == null) || table.isDisposed()) {
                return result;
            }
            result = new int[table.getColumnCount()];
            int i = 0;
            TableColumn[] cols = table.getColumns();
            for (TableColumn col : cols) {
                result[i++] = col.getAlignment();
            }
        } catch (Exception exc) {
            logger.error(exc.getMessage(), exc);
            return null;
        }
        return result;
    }

    public static void checkAll(final Table table, final boolean checked) {
        try {
            if ((table == null) || table.isDisposed() || ((table.getStyle() & SWT.CHECK) == 0)) {
                return;
            }
            TableItem[] items = table.getItems();
            if (items == null) {
                return;
            }
            final int LENGTH = items.length;
            for (int i = 0; i < LENGTH; i++) {
                items[i].setChecked(checked);
            }
        } catch (Exception exc) {
            logger.error(exc.getMessage(), exc);
        }
    }

    public static TableItem[] getCheckedTableItems(final Table table) {
        TableItem[] checkedItems = null;
        List<TableItem> vecChecked;
        try {
            vecChecked = WidgetTableUtil.getCheckedItems(table);
            if (!vecChecked.isEmpty()) {
                checkedItems = new TableItem[vecChecked.size()];
                for (int i = 0; i < vecChecked.size(); i++) {
                    checkedItems[i] = vecChecked.get(i);
                }
            }
        } catch (Exception exc) {
            logger.error(exc.getMessage(), exc);
            return null;
        }
        return checkedItems;
    }

    private static void addTableColumnsResizeObserver(final Table table, final Class<?> clazz, final String tableKey) {
        if ((table == null) || table.isDisposed() || (table.getColumnCount() == 0) || (clazz == null)) {
            return;
        }
        final TableColumn[] cols = table.getColumns();
        for (final TableColumn col : cols) {
            col.addListener(SWT.Resize, new Listener() {
                @Override
                public final void handleEvent(final Event e) {
                    TableSetting setting = SettingsController.getTableSetting(cols.length, clazz, tableKey);
                    int[] dims = setting.getWidths();
                    if (col.getWidth() > 0) {
                        dims[table.indexOf(col)] = col.getWidth();
                        SettingsController.saveTableConfig(setting);
                    }
                    col.setResizable(col.getWidth() > 0);
                }
            });
        }
    }

    private static void addTableColumnsOrderListener(final Table table, final Class<?> clazz, final String tableKey) {
        if ((table == null) || table.isDisposed() || (table.getColumnCount() == 0) || (clazz == null)) {
            return;
        }
        final TableColumn[] cols = table.getColumns();
        for (TableColumn col : cols) {
            col.addListener(SWT.Move, new Listener() {
                @Override
                public final void handleEvent(final Event e) {
                    TableSetting tableSetting = SettingsController.getTableSetting(cols.length, clazz, tableKey);
                    tableSetting.setOrder(table.getColumnOrder());
                    SettingsController.saveTableConfig(tableSetting);
                }
            });
        }
    }

    private static Menu createTableHeaderMenu(final Table table, final Class<?> clazz, final Menu tableMenu, final String tableKey) {
        if ((table == null) || table.isDisposed() || (clazz == null)) {
            return null;
        }
        final Menu headerMenu = new Menu(table.getShell(), SWT.POP_UP);
        headerMenu.setData("header");
        try {
            String[] colNames = WidgetTableUtil.getColumnsNames(table);
            headerMenu.addListener(SWT.Show, new Listener() {
                @Override
                public final void handleEvent(final Event e) {
                    final boolean[] visibleCols = SettingsController.getTableSetting(table.getColumnCount(), clazz, tableKey).getVisibility();
                    for (int i = 2; i < headerMenu.getItemCount(); i++) {
                        headerMenu.getItem(i).setSelection(visibleCols[table.getColumnOrder()[i - 2]]);
                    }
                }
            });

            final MenuItem itemAdvanced = new MenuItem(headerMenu, SWT.PUSH);
            itemAdvanced.setText("Selectie avansata");
            itemAdvanced.addListener(SWT.Selection, new Listener() {
                @Override
                public final void handleEvent(final Event e) {
                    new ColumnsChooserView(table, clazz, tableKey).open();
                }
            });

            new MenuItem(headerMenu, SWT.SEPARATOR);

            for (int i = 0; i < colNames.length; i++) {
                final MenuItem item = new MenuItem(headerMenu, SWT.CHECK);
                item.setText(colNames[i]);
                item.addListener(SWT.Selection, new Listener() {
                    @Override
                    public final void handleEvent(final Event e) {
                        TableSetting tableSetting = SettingsController.getTableSetting(table.getColumnCount(), clazz, tableKey);
                        final int[] savedDims = tableSetting.getWidths();
                        boolean[] visible = tableSetting.getVisibility();
                        final int itemIndex = headerMenu.indexOf(item) - 2;
                        if (item.getSelection()) {
                            table.getColumn(table.getColumnOrder()[itemIndex]).setWidth(savedDims[table.getColumnOrder()[itemIndex]]);
                            visible[table.getColumnOrder()[itemIndex]] = true;
                            table.getColumn(table.getColumnOrder()[itemIndex]).setResizable(true);
                            table.getColumn(table.getColumnOrder()[itemIndex]).setMoveable(true);
                        } else {
                            table.getColumn(table.getColumnOrder()[itemIndex]).setWidth(0);
                            visible[table.getColumnOrder()[itemIndex]] = false;
                            table.getColumn(table.getColumnOrder()[itemIndex]).setResizable(false);
                            table.getColumn(table.getColumnOrder()[itemIndex]).setMoveable(false);
                        }
                        tableSetting.setVisibility(visible);
                        SettingsController.saveTableConfig(tableSetting);
                    }
                });
            }

            table.addListener(SWT.Dispose, new Listener() {
                @Override
                public void handleEvent(final Event event) {
                    headerMenu.dispose();
                    if (tableMenu != null) {
                        tableMenu.dispose();
                    }
                }
            });
        } catch (Exception exc) {
            logger.error(exc.getMessage(), exc);
            SWTeXtension.displayMessageE(exc.getMessage(), exc);
        }
        return headerMenu;
    }

    /**
     * @param table some real non-null, non-disposed table. If null or disposed, method will return
     *              quietely
     * @param clazz some class
     */
    public static void customizeTable(final Table table, final Class<?> clazz) {
        WidgetTableUtil.customizeTable(table, clazz, null);
    }

    /**
     * @param table  some real non-null, non-disposed table. If null or disposed, method will return
     *               quietely
     * @param clazz  some class
     * @param sufix2 a string delimiter to create unique que on the specified class. May be ignored if
     *               class has
     *               a single grid
     *               The method calls {@link #customizeTable(Table, Class, String, int)} with all three
     *               available flags
     */
    public static void customizeTable(final Table table, final Class<?> clazz, final String sufix2) {
        WidgetTableUtil.customizeTable(table, clazz, sufix2, WidgetTableUtil.ADD_RESIZE | WidgetTableUtil.ADD_HEADER | WidgetTableUtil.ADD_ORDER);
    }

    /**
     * @param table    some real non-null, non-disposed table. If null or disposed, method will return
     *                 quietely
     * @param clazz    some class
     * @param tableKey a string delimiter to create unique que on the specified class. May be ignored if
     *                 class has
     *                 a single grid
     * @param flags    at least one options, or this method will be a dummy call
     */
    public static void customizeTable(final Table table, final Class<?> clazz, final String tableKey, final int flags) {
        if ((table == null) || table.isDisposed() || (clazz == null)) {
            return;
        }
        if ((flags & WidgetTableUtil.ADD_RESIZE) == WidgetTableUtil.ADD_RESIZE) {
            WidgetTableUtil.addTableColumnsResizeObserver(table, clazz, tableKey);
        }
        if ((flags & WidgetTableUtil.ADD_HEADER) == WidgetTableUtil.ADD_HEADER) {
            // TODO metoda are un bug cand se da click pe header prima data, nu mai apare meniul initial al tabelei

            table.addListener(SWT.MenuDetect, new Listener() {
                @Override
                public void handleEvent(final Event event) {
                    Point pt = table.getDisplay().map(null, table, new Point(event.x, event.y));
                    Rectangle clientArea = table.getClientArea();
                    boolean header = (clientArea.y <= pt.y) && (pt.y < (clientArea.y + table.getHeaderHeight()));
                    Menu headerMenu = (Menu) table.getData("header");
                    Menu currentMenu = table.getMenu();
                    if ((currentMenu != null) && !currentMenu.isDisposed()) {
                        if (!currentMenu.equals(headerMenu)) {
                            table.setData("menu", currentMenu);
                        }
                    }
                    if (header) {
                        if ((table.getMenu() != null) && "header".equals(table.getMenu().getData())) {
                            table.getMenu().dispose();
                        }
                        headerMenu = WidgetTableUtil.createTableHeaderMenu(table, clazz, table.getMenu(), tableKey);
                        table.setData("header", headerMenu);
                    }
                    if (header) {
                        table.setMenu(headerMenu);
                    } else {
                        Menu menu = (Menu) table.getData("menu");
                        if ((menu != null) && !menu.isDisposed()) {
                            if (menu.equals(headerMenu)) {
                                table.setMenu(null);
                            } else {
                                table.setMenu(menu);
                            }
                        } else if ((table.getMenu() != null) && !table.getMenu().isDisposed()) {
                            if (table.getMenu().equals(headerMenu)) {
                                table.setMenu(null);
                            } else {
                                table.setData("menu", table.getMenu());
                            }
                        }
                    }
                }
            });
        }
        if ((flags & WidgetTableUtil.ADD_ORDER) == WidgetTableUtil.ADD_ORDER) {
            WidgetTableUtil.addTableColumnsOrderListener(table, clazz, tableKey);
            TableSetting setting = SettingsController.getTableSetting(table.getColumnCount(), clazz, tableKey);
            table.setColumnOrder(setting.getOrder());
        }
    }
}
