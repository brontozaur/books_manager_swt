package com.papao.books.view.util;

import com.papao.books.view.AppImages;
import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.*;

public final class WidgetMenuUtil {
	
	private static Logger logger = Logger.getLogger(WidgetMenuUtil.class);

    private WidgetMenuUtil() {}

    public static Menu createExportMenu(final MenuItem menuItem, final Table table, final String reportName, final Class<?> clazz, final String sufix) {
        MenuItem item;
        try {
            if ((menuItem == null) || menuItem.isDisposed() || (table == null) || table.isDisposed()) {
                return null;
            }
            Menu menuExport = new Menu(menuItem);
            menuItem.setMenu(menuExport);

            item = new MenuItem(menuExport, SWT.NULL);
            item.setText("Export fisier PDF");
            item.setImage(AppImages.getImage16(AppImages.IMG_ADOBE));
            item.addListener(SWT.Selection, new Listener() {
                @Override
                public final void handleEvent(final Event e) {
//                    Exporter.export(ExportType.PDF, table, reportName, clazz, sufix);
                }
            });

            item = new MenuItem(menuExport, SWT.NULL);
            item.setText("Export fisier Excel");
            item.setImage(AppImages.getImage16(AppImages.IMG_EXCEL));
            item.addListener(SWT.Selection, new Listener() {
                @Override
                public final void handleEvent(final Event e) {
//                    Exporter.export(Exporter.XLS, table, reportName, clazz, sufix);
                }
            });

            item = new MenuItem(menuExport, SWT.NULL);
            item.setText("Export fisier TXT");
            item.setImage(AppImages.getImage16(AppImages.IMG_EXPORT));
            item.addListener(SWT.Selection, new Listener() {
                @Override
                public final void handleEvent(final Event e) {
//                    Exporter.export(Exporter.TXT, table, reportName, clazz, sufix);
                }
            });

            item = new MenuItem(menuExport, SWT.NULL);
            item.setText("Export fisier RTF");
            item.setImage(AppImages.getImage16(AppImages.IMG_WORD2));
            item.addListener(SWT.Selection, new Listener() {
                @Override
                public final void handleEvent(final Event e) {
//                    Exporter.export(Exporter.RTF, table, reportName, clazz, sufix);
                }
            });

            item = new MenuItem(menuExport, SWT.NULL);
            item.setText("Export fisier HTML");
            item.setImage(AppImages.getImage16(AppImages.IMG_BROWSER));
            item.addListener(SWT.Selection, new Listener() {
                @Override
                public final void handleEvent(final Event e) {
//                    Exporter.export(Exporter.HTML, table, reportName, clazz, sufix);
                }
            });

            return menuExport;
        } catch (Exception exc) {
            logger.error(exc.getMessage(), exc);
            return null;
        }
    }

    public static void customizeMenuLocation(final Menu menu, final ToolItem toolItem) {
        try {
            if ((menu == null) || menu.isDisposed()) {
                return;
            }
            if ((toolItem == null) || toolItem.isDisposed() || (toolItem.getParent().getMenu() != menu)) {
                return;
            }
            final Rectangle itemBounds = toolItem.getBounds();
            final Point point = toolItem.getParent().toDisplay(new Point(itemBounds.x, itemBounds.y));
            menu.setLocation(point.x, point.y + itemBounds.height);
            menu.setVisible(false);
        } catch (Exception exc) {
            logger.error(exc.getMessage(), exc);
        }
    }

    public static void customizeMenuLocation(final Menu menu, final Control controlMenu) {
        try {
            if ((menu == null) || menu.isDisposed()) {
                return;
            }
            if ((controlMenu == null) || controlMenu.isDisposed() || (controlMenu.getMenu() != menu)) {
                return;
            }
            final Rectangle itemBounds = controlMenu.getBounds();
            final Point point = controlMenu.toDisplay(new Point(itemBounds.x, itemBounds.y));
            menu.setLocation(point.x - itemBounds.width, point.y + itemBounds.height);
            menu.setVisible(false);
        } catch (Exception exc) {
            logger.error(exc.getMessage(), exc);
        }
    }
}
