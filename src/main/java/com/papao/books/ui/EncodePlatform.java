package com.papao.books.ui;

import com.papao.books.BooksApplication;
import com.papao.books.FiltruAplicatie;
import com.papao.books.auth.LoginShell;
import com.papao.books.ui.view.AbstractCViewAdapter;
import com.papao.books.ui.view.AbstractView;
import com.papao.books.ui.view.SWTeXtension;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ViewForm;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import java.awt.*;
import java.util.Calendar;

public class EncodePlatform extends AbstractCViewAdapter implements Listener {

    private static Logger logger = Logger.getLogger(EncodePlatform.class);
    private ViewForm appMainForm;
    private ToolTip appToolTip;
    private Tray appTray;
    public static EncodePlatform instance;

    /**
     * The core of the sistem.
     */
    public EncodePlatform() {
        super(null, AbstractView.MODE_NONE);
        /**
         * linia asta ne scapa de o intrebare tampita, si falsa, cauzata de listenerul pe SWT.Close
         * din AbstractView,
         * de pe shell. Avem nevoie de acest listener, pt a nu repeta codul care salveaza
         * dimensiunea si coordonatele
         * shell-ului, ca sa-l redeschida pe ultimele valori setate, daca e bifata chestia asta in
         * configurare.
         */
        try {
            setExitChoice(SWT.OK);
            setAddCloseListener(true);
            EncodePlatform.instance = this;
            setUseDocking(false);

            getShell().setImage(AppImages.getImage16(AppImages.IMG_BORG_MAIN));
            GridLayoutFactory.fillDefaults().numColumns(1).equalWidth(true).margins(0, 0).spacing(SWT.DEFAULT, 0).applyTo(getShell());
            getShell().addListener(SWT.Close, this);
            getShell().setImages(new Image[]{
                    AppImages.getImage16(AppImages.IMG_BORG_MAIN), AppImages.getImage24(AppImages.IMG_BORG_MAIN),
                    AppImages.getImage32(AppImages.IMG_BORG_MAIN)});

            createTraySystem();
            getContainer().dispose();

            this.appMainForm = new ViewForm(getShell(), SWT.FLAT);
            if (!FiltruAplicatie.isWindowsUsingCoords()) {
                int width;
                int height;

                if (Display.getDefault().getPrimaryMonitor().getBounds().width > 1280) {
                    width = 1280;
                } else if (Display.getDefault().getPrimaryMonitor().getBounds().width > 1024) {
                    width = 1024;
                } else {
                    width = 800;
                }

                if (Display.getDefault().getPrimaryMonitor().getBounds().height > 1024) {
                    height = 800;
                } else if (Display.getDefault().getPrimaryMonitor().getBounds().height > 768) {
                    height = 600;
                } else {
                    height = 480;
                }
                GridDataFactory.fillDefaults().grab(true, true).align(SWT.FILL, SWT.FILL).hint(width, height).applyTo(getAppMainForm());
            } else {
                GridDataFactory.fillDefaults().grab(true, true).align(SWT.FILL, SWT.FILL).applyTo(getAppMainForm());
            }
            GridLayoutFactory.fillDefaults().numColumns(1).equalWidth(true).applyTo(getAppMainForm());

        } catch (Exception exc) {
            BooksApplication.closeApp(true);
        }
    }

    private final void createTraySystem() {
        TrayItem item;
        MenuItem menuItem;

        try {
            if (!SystemTray.isSupported()) {
                return;
            }
            getShell().addListener(SWT.Iconify, this);

            final Menu trayItemMenu = new Menu(getShell(), SWT.POP_UP);

            this.appToolTip = new ToolTip(getShell(), SWT.BALLOON | SWT.ICON_INFORMATION);
            this.appToolTip.setAutoHide(true);

            this.appTray = Display.getDefault().getSystemTray();
            if (getAppTray() == null) {
                logger.warn("System tray is not available...");
                return;
            }

            item = new TrayItem(getAppTray(), SWT.NONE);
            item.setToolTipText(getShellText());
            item.setImage(AppImages.getImage16(AppImages.IMG_BORG_MAIN));
            item.setToolTip(this.appToolTip);
            item.addListener(SWT.DefaultSelection, new Listener() {
                @Override
                public void handleEvent(final Event event) {
                    if (getShell().isVisible()) {
                        getShell().setFocus();
                    } else {
                        getShell().open();
                        getShell().setMinimized(false);
                    }
                }
            });

            menuItem = new MenuItem(trayItemMenu, SWT.PUSH);
            menuItem.setText("Afisare Encode Borg");
            menuItem.setImage(AppImages.getImage16(AppImages.IMG_BORG_MAIN));
            menuItem.addListener(SWT.Selection, new Listener() {
                @Override
                public void handleEvent(final Event event) {
                    if (getShell().isVisible()) {
                        getShell().setFocus();
                    } else {
                        getShell().open();
                        getShell().setMinimized(false);
                    }
                }
            });
            trayItemMenu.setDefaultItem(menuItem);

            menuItem = new MenuItem(trayItemMenu, SWT.PUSH);
            menuItem.setText("Schimbare firma/data/utilizator");
            menuItem.setImage(AppImages.getImage16(AppImages.IMG_USER));
            menuItem.addListener(SWT.Selection, new Listener() {
                @Override
                public final void handleEvent(final Event event) {
                    logout(true);
                }
            });

            new MenuItem(trayItemMenu, SWT.SEPARATOR);

            menuItem = new MenuItem(trayItemMenu, SWT.PUSH);
            menuItem.setText("Inchide aplicatia");
            menuItem.setImage(AppImages.getImage16(AppImages.IMG_STOP));
            menuItem.addListener(SWT.Selection, new Listener() {
                @Override
                public final void handleEvent(final Event event) {
                    performShellClose(event);
                }
            });

            item.addListener(SWT.MenuDetect, new Listener() {
                @Override
                public final void handleEvent(final Event event) {
                    if (!trayItemMenu.isDisposed()) {
                        trayItemMenu.setVisible(true);
                    }
                }
            });

        } catch (Exception exc) {
            logger.error(exc.getMessage(), exc);
        }
    }

    public final void performShellClose(Event e) {
        try {
            if (e.type != SWT.Close) {
                e.type = SWT.Close;
            }
            boolean close = SWTeXtension.displayMessageQ("Sunteti sigur ca doriti sa inchideti aplicatia?", "Inchidere aplicatie") == SWT.NO;
            if (close) {
                e.doit = false;
            } else {
                getShell().removeListener(SWT.Close, this);
                getShell().notifyListeners(SWT.Close, e);
                BooksApplication.closeApp(false);
            }
        } catch (Exception exc) {
            logger.error(exc.getMessage(), exc);
        }
    }

    public final void logout(final boolean ask) {
        try {
            if (ask
                    && (SWTeXtension.displayMessageQ("Sunteti sigur ca doriti schimbarea firmei/utilizatorului/datei curente?", "Confirmare relogare") == SWT.NO)) {
                return;
            }
            getShell().removeListener(SWT.Close, this);
            close(SWT.OK);
            if (getAppTray() != null) {
                getAppTray().dispose();
            }

            new LoginShell().open(false, true);
        } catch (Exception exc) {
            logger.error(exc.getMessage(), exc);
        }
    }

    @Override
    public final void handleEvent(final Event e) {
        if (e.type == SWT.Close) {
            if (e.widget == getShell()) {
                performShellClose(e);
            }
        } else if (e.type == SWT.Iconify) {
            if (e.widget == getShell()) {
                getShell().setVisible(false);
            }
        }
    }

    public final void showTrayMessage(final String message) {
        if ((this.appToolTip == null) || this.appToolTip.isDisposed() || StringUtils.isEmpty(message)) {
            return;
        }
        this.appToolTip.setMessage(message);
        this.appToolTip.setVisible(true);
    }

    public final void closeTrayMessage() {
        if ((getAppTray() != null) && (getAppTray().getItemCount() > 0)) {
            getAppTray().getItem(0).setVisible(false);
            getAppTray().getItem(0).setVisible(true);
        }
    }

    public final ViewForm getAppMainForm() {
        return this.appMainForm;
    }

    public final Tray getAppTray() {
        return this.appTray;
    }

    @Override
    public void customizeView() {
        StringBuilder strName;
        strName = new StringBuilder();
        strName.append("Books Manager");
        strName.append(" [ora logare : ");
        Calendar cal = Calendar.getInstance();
        if (cal.get(Calendar.HOUR_OF_DAY) < 10) {
            strName.append('0');
        }
        strName.append(cal.get(Calendar.HOUR_OF_DAY));
        strName.append(':');
        if (cal.get(Calendar.MINUTE) < 10) {
            strName.append('0');
        }
        strName.append(cal.get(Calendar.MINUTE));
        strName.append(':');
        if (cal.get(Calendar.SECOND) < 10) {
            strName.append('0');
        }
        strName.append(cal.get(Calendar.SECOND));
        strName.append("]");

        setShellText(strName.toString());
    }
}
