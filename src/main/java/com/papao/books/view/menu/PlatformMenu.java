package com.papao.books.view.menu;

import com.papao.books.BooksApplication;
import com.papao.books.view.AppImages;
import com.papao.books.view.EncodePlatform;
import com.papao.books.view.auth.EncodeLive;
import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.*;

/**
 * basic app menus Clasa descrie crearea meniului principal al aplicatiei, si este
 * responsabila de crearea meniului pentru modulul de Configurare.
 */
public final class PlatformMenu {

	private static Logger logger = Logger.getLogger(PlatformMenu.class);

    private PlatformMenu() {
    }

    public static Menu createShellMenu() {
        Menu menu = null;
        menu = new Menu(EncodePlatform.getInstance().getShell(), SWT.BAR);

        final MenuItem itemAuth = new MenuItem(menu, SWT.CASCADE);
        itemAuth.setText("&Utilizatori");
        itemAuth.setMenu(PlatformMenu.createMenuAuth(itemAuth));

        final MenuItem window = new MenuItem(menu, SWT.CASCADE);
        window.setText("Fe&reastra");
        window.setMenu(PlatformMenu.createWindowMenu(window));

        final MenuItem itemAbout = new MenuItem(menu, SWT.CASCADE);
        itemAbout.setText("&Help");
        itemAbout.setMenu(PlatformMenu.createAboutMenu(itemAbout));

        return menu;
    }

    public static Menu createMenuAuth(final MenuItem item) {
        Menu menuAuth = null;
        menuAuth = new Menu(item);
        MenuItem subItem;
        subItem = new MenuItem(menuAuth, SWT.PUSH);
        subItem.setText("Logout");
        subItem.setAccelerator(SWT.ALT + 'F');
        subItem.setImage(AppImages.getImage16(AppImages.IMG_HOME));
        subItem.addListener(SWT.Selection, new Listener() {
            @Override
            public final void handleEvent(final Event e) {
                BooksApplication.getInstance().open();
            }
        });

        new MenuItem(menuAuth, SWT.SEPARATOR);

        subItem = new MenuItem(menuAuth, SWT.PUSH);
        subItem.setText("Exit\tAlt+Q");
        subItem.setImage(AppImages.getImage16(AppImages.IMG_STOP));
        subItem.setAccelerator(new Integer(EncodeLive.IS_MAC ? SWT.COMMAND : SWT.CTRL) + 'Q');
        subItem.addListener(SWT.Selection, new Listener() {
            @Override
            public final void handleEvent(final Event e) {
                EncodePlatform.getInstance().performShellClose(new Event());
            }
        });
        return menuAuth;
    }

    public static Menu createWindowMenu(final MenuItem item) {
        final Menu windowMenu = new Menu(item);
        windowMenu.addListener(SWT.Show, new Listener() {
            @Override
            public final void handleEvent(final Event e) {
                int idx = 0;
                windowMenu.getItem(idx++).setSelection(item.getParent().getShell().getMaximized());
                windowMenu.getItem(idx++).setSelection(!item.getParent().getShell().getMaximized());
            }
        });

        final MenuItem maxItem = new MenuItem(windowMenu, SWT.CHECK);
        maxItem.setText("Maximizare\tAlt+Enter");
        maxItem.setAccelerator(SWT.ALT + SWT.CR);
        maxItem.setImage(AppImages.getImageMiscByName(AppImages.IMG_MISC_SIMPLE_MAXIMIZE));
        maxItem.addListener(SWT.Selection, new Listener() {
            @Override
            public final void handleEvent(final Event e) {
                Shell parent = (Shell) maxItem.getParent().getParent();
                parent.setMaximized(true);
            }
        });

        final MenuItem minItem = new MenuItem(windowMenu, SWT.CHECK);
        minItem.setText("Minimizare\tCtrl+Enter");
        minItem.setAccelerator(SWT.CTRL + SWT.CR);
        minItem.setImage(AppImages.getImageMiscByName(AppImages.IMG_MISC_SIMPLE_MINIMIZE));
        minItem.addListener(SWT.Selection, new Listener() {
            @Override
            public final void handleEvent(final Event e) {
                Shell parent = (Shell) maxItem.getParent().getParent();
                parent.setMaximized(false);
            }
        });

        return windowMenu;
    }

    public static Menu createAboutMenu(final MenuItem item) {
        Menu aboutMenu;
        aboutMenu = new Menu(item);

        final MenuItem helpItem = new MenuItem(aboutMenu, SWT.PUSH);
        helpItem.setText("Help\tF1");
        helpItem.setAccelerator(SWT.F1);
        helpItem.setImage(AppImages.getImage16(AppImages.IMG_HELP));
        helpItem.addListener(SWT.Selection, new Listener() {
            @Override
            public final void handleEvent(final Event e) {
                PlatformMenu.launchHelp();
            }
        });

        new MenuItem(aboutMenu, SWT.SEPARATOR);

        final MenuItem aboutItem = new MenuItem(aboutMenu, SWT.PUSH);
        aboutItem.setText("Despre...");
        aboutItem.setImage(AppImages.getImageMiscByName(AppImages.IMG_MISC_HYPERCUBE_ICO));
        aboutItem.addListener(SWT.Selection, new Listener() {
            @Override
            public final void handleEvent(final Event e) {
                new Thread(new CreditsView(aboutItem.getParent().getShell())).start();
                // SAXParserExample example = new SAXParserExample();
                // example.runExample();

            }
        });
        return aboutMenu;
    }

    public static void launchHelp() {
        try {
            WebBrowser hb = new WebBrowser(null, "https://www.goodreads.com", false);
            hb.open(true, false);
        } catch (Exception exc) {
            logger.error(exc.getMessage(), exc);
        }
    }

}