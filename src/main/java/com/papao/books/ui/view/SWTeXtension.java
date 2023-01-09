package com.papao.books.ui.view;

import com.papao.books.ui.AppImages;
import com.papao.books.ui.auth.EncodeLive;
import com.papao.books.ui.interfaces.*;
import com.papao.books.ui.util.ColorUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.*;
import org.mihalis.opal.notify.Notifier;

public final class SWTeXtension {

    private static final String ERR_MSG = "A intervenit o eroare.";
    private static final String ERR_BOLD_MSG = "Eroare în aplicație";

    private static final Logger logger = Logger.getLogger(SWTeXtension.class);

    /**
     * prevents instantiation
     */
    private SWTeXtension() {
    }

    private static int displayMessage(final String message, final String boldMessage, final int style, final Throwable th, final String canonicalFilePath) {
        MsgBox mesaj = null;
        if ((style & SWT.ICON_ERROR) != 0) {
            int style_error = SWT.OK;
            if (th != null) {
                style_error |= SWT.BUTTON1;
            }
            mesaj = new MsgBox(style_error, message, boldMessage, SWT.ICON_ERROR, th, canonicalFilePath);
        } else if ((style & SWT.ICON_INFORMATION) != 0) {
            int style_info = SWT.OK;
            if (StringUtils.isNotEmpty(canonicalFilePath)) {
                style_info |= SWT.BUTTON2;
            }
            mesaj = new MsgBox(style_info, message, boldMessage, SWT.ICON_INFORMATION, null, canonicalFilePath);
        } else if ((style & SWT.ICON_WARNING) != 0) {
            mesaj = new MsgBox(SWT.OK, message, boldMessage, SWT.ICON_WARNING, null, null);
        } else if ((style & SWT.ICON_QUESTION) != 0) {
            mesaj = new MsgBox(SWT.YES | SWT.NO, message, boldMessage, SWT.ICON_QUESTION, null, null);
        }
        if ((mesaj == null) || mesaj.isDisposed()) {
            mesaj = new MsgBox(SWT.OK, message, boldMessage, SWT.ICON_INFORMATION, null, canonicalFilePath);
        }
        mesaj.open(false);
        return mesaj.getResult();
    }

    /*
     * afisare intrebare
     */
    public static int displayMessageQ(final String mesaj) {
        return SWTeXtension.displayMessageQ(mesaj, "Confirmare acțiune");
    }

    public static int displayMessageQ(final String mesaj, final String boldMessage) {
        return SWTeXtension.displayMessage(mesaj, boldMessage, SWT.ICON_QUESTION, null, null);
    }

    /*
     * afisare avertisment
     */
    public static void displayMessageW(final String mesaj) {
        if (EncodeLive.isNotificationUsingOpalStyle()) {
            Notifier.getSettings().setIconStyle(SWT.ICON_WARNING);
            Notifier.notify("Avertizare", mesaj);
        } else {
            SWTeXtension.displayMessageW(mesaj, null);
        }
    }

    public static void displayMessageW(final String mesaj, final String boldMessage) {
        if (EncodeLive.isNotificationUsingOpalStyle()) {
            Notifier.getSettings().setIconStyle(SWT.ICON_WARNING);
            Notifier.notify(boldMessage, mesaj);
        } else {
            SWTeXtension.displayMessage(mesaj, boldMessage, SWT.ICON_WARNING, null, null);
        }
    }

    /*
     * afisare informatii
     */

    public static void displayMessageI(final String mesaj) {
        if (EncodeLive.isNotificationUsingOpalStyle()) {
            Notifier.getSettings().setIconStyle(SWT.ICON_INFORMATION);
            Notifier.notify("Notificare", mesaj);
        } else {
            SWTeXtension.displayMessageI(mesaj, null, null);
        }
    }

    public static void displayMessageI(final String mesaj, final String boldMessage) {
        if (EncodeLive.isNotificationUsingOpalStyle()) {
            Notifier.getSettings().setIconStyle(SWT.ICON_INFORMATION);
            Notifier.notify(boldMessage, mesaj);
        } else {
            SWTeXtension.displayMessageI(mesaj, boldMessage, null);
        }
    }

    public static void displayMessageI(final String mesaj, final String boldMessage, final String canonicalFileName) {
        SWTeXtension.displayMessage(mesaj, boldMessage, SWT.ICON_INFORMATION, null, canonicalFileName);
    }

    /*
     * afisare mesaj eroare
     */
    public static void displayMessageE(final String mesaj, final Throwable th) {
        if (th == null && EncodeLive.isNotificationUsingOpalStyle()) {
            Notifier.getSettings().setIconStyle(SWT.ICON_ERROR);
            Notifier.notify("Eroare", mesaj);
        } else {
            SWTeXtension.displayMessageE(mesaj, null, th);
        }
    }

    public static void displayMessageEGeneric(final Throwable th) {
        if (th == null && EncodeLive.isNotificationUsingOpalStyle()) {
            Notifier.getSettings().setIconStyle(SWT.ICON_ERROR);
            Notifier.notify(SWTeXtension.ERR_BOLD_MSG, SWTeXtension.ERR_MSG);
        } else {
            SWTeXtension.displayMessageE(SWTeXtension.ERR_MSG, SWTeXtension.ERR_BOLD_MSG, th);
        }
    }

    public static void displayMessageE(final String mesaj, final String boldMessage, final Throwable th) {
        if (th == null && EncodeLive.isNotificationUsingOpalStyle()) {
            Notifier.getSettings().setIconStyle(SWT.ICON_ERROR);
            Notifier.notify(boldMessage, mesaj);
        } else {
            SWTeXtension.displayMessage(mesaj, boldMessage, SWT.ICON_ERROR, th, null);
        }
    }

    /**
     * Acest tip de MessageBox se va folosi in cazurile in care nu se shtie cu exactitate daca imaginile aplicatiei sunt disponibile sau nu, sau daca se shtie cu certitudine ca acestea NU sunt
     * disponibile. Cum foloseshte o clasa din SWT, daca nici o versiune a librariei nu este disponibila in path, va arunca o eroare, care va fi afisata de handler-ul UncaughtExceptionHandler. Daca
     * mecanismul de loguri este disponibil, eroarea va fi scrisa in loguri. TODO de facut eventual posibila generarea de fisiere folosind un FileOutputSteam, al carui input va fi consola de erori.
     *
     * @param msg mesajul care va fi afisat.
     */
    public static void showErrorBoxSWTInternal(final String msg) {
        MessageBox box = new MessageBox(new Shell(Display.getCurrent()), SWT.ICON_ERROR);
        if (StringUtils.isNotEmpty(msg)) {
            box.setMessage(msg);
            box.setText("Eroare");
        }
        box.open();
    }

    public static void addColoredFocusListener(final Control widget, final Color color) {
        if ((widget == null) || widget.isDisposed()) {
            return;
        }
        try {
            widget.addFocusListener(new FocusAdapter() {

                @Override
                public void focusGained(final FocusEvent event) {
                    try {
                        Control control = (Control) event.widget;
                        if (control.isDisposed()) {
                            return;
                        }
                        if (control instanceof Text) {
                            if (!((Text) event.widget).getEditable()) {
                                return;
                            }
                        }
                        if (control instanceof Button) {
                            return;
                        }
                        control.setBackground(((color != null) && !color.isDisposed()) ? color : ColorUtil.COLOR_FOCUS_YELLOW);
                        if (event.widget instanceof Text) {
                            ((Text) event.widget).selectAll();
                        }
                    } catch (Exception exc) {
                        logger.error(exc.getMessage(), exc);
                    }
                }

                @Override
                public void focusLost(final FocusEvent event) {
                    try {
                        if (((Control) event.widget).isDisposed()) {
                            return;
                        }
                        ((Control) event.widget).setBackground(null);
                        if (event.widget instanceof Text) {
                            ((Text) event.widget).setSelection(0, 0);
                        }
                    } catch (Exception exc) {
                        logger.error(exc.getMessage(), exc);
                    }
                }
            });
        } catch (Exception exc) {
            logger.error(exc.getMessage(), exc);
        }
    }

    public static void setGradientBackground(final Shell shell, final Color c1, final Color c2) {
        try {
            if ((shell == null) || shell.isDisposed()) {
                return;
            }
            if ((c1 == null) || c1.isDisposed()) {
                return;
            }
            if ((c2 == null) || c2.isDisposed()) {
                return;
            }
            Rectangle rect = shell.getClientArea();
            if ((shell.getBackground() != null) && !shell.getBackground().isDisposed()) {
                shell.getBackground().dispose();
            }
            Image newImage = new Image(Display.getDefault(), Math.max(1, rect.width), 1);
            GC gc = new GC(newImage);
            gc.setForeground(c1);
            gc.setBackground(c2);
            gc.fillGradientRectangle(rect.x, rect.y, rect.width, 1, false);
            gc.dispose();
            if ((shell.getBackgroundImage() != null) && !shell.getBackgroundImage().isDisposed()) {
                shell.getBackgroundImage().dispose();
            }
            shell.setBackgroundImage(newImage);
        } catch (Exception exc) {
            logger.error(exc.getMessage(), exc);
        }
    }

    public static void setGradientBackground(final Control control, final Color c1, final Color c2) {
        try {
            if ((control == null) || control.isDisposed()) {
                return;
            }
            if ((c1 == null) || c1.isDisposed()) {
                return;
            }
            if ((c2 == null) || c2.isDisposed()) {
                return;
            }
            Rectangle rect = control.getBounds();
            Image newImage = null;
            if (control.getParent() == null) {
                newImage = new Image(Display.getDefault(), Math.max(1, rect.width), 1);
            } else {
                newImage = new Image(Display.getDefault(), control.getParent().getBackgroundImage().getImageData());
            }
            if ((control.getBackgroundImage() != null) && !control.getBackgroundImage().isDisposed()) {
                control.getBackgroundImage().dispose();
            }
            GC gc = new GC(newImage);
            gc.setForeground(c1);
            gc.setBackground(c2);
            gc.fillGradientRectangle(rect.x, rect.y, rect.width, 1, false);
            gc.dispose();
            if ((control.getBackgroundImage() != null) && !control.getBackgroundImage().isDisposed()) {
                control.getBackgroundImage().dispose();
            }
            control.setBackgroundImage(newImage);
        } catch (Exception e) {
            if ((control == null) || control.isDisposed()) {
                return;
            }
        }
    }

    public static void addGradientListener(final Shell shell, final Color c1, final Color c2) {
        try {
            if ((shell == null) || shell.isDisposed()) {
                return;
            }
            if ((c1 == null) || c1.isDisposed()) {
                return;
            }
            if ((c2 == null) || c2.isDisposed()) {
                return;
            }
            /**
             * initializare gradient
             */
            SWTeXtension.setGradientBackground(shell, c1, c2);
            /**
             * listener pentru recalcul gradient pe ev de resize
             */
            shell.addListener(SWT.Resize, new Listener() {

                @Override
                public void handleEvent(final Event event) {
                    SWTeXtension.setGradientBackground(shell, c1, c2);
                }
            });
        } catch (Exception exc) {
            logger.error(exc.getMessage(), exc);
        }
    }

    /**
     * O metoda foarte interesanta de a mosteni imaginea background gradient a parintelui. Are sens doar pentru componente care au setat un GridData, cu prop FILL_HORIZONTAL, pentru ca altfel, la
     * redimensionarea parintelui, widgetul curent ajunge la adancime maxima de culoare la marginea din dreapta, pe cand densitatea culorii parintelui scade, ceea ce este inestetic. Daca nu exista
     * prop FILL_HORIZONTAL (sau orice fel de fill pe evenimentul de resize), efectul este OK si se aplica, dar il recomand doar pe shell-uri fara SWT.MAX. Se recomanda, pana la alte imbunatatiri ale
     * metodei folosirea unor culori FOARTE deschide atat pentru addGradient(final Shell shell) cat si pentru addGradient2Widget(final Control control) (obligatoriu aceleasi 2 culori in ambele
     * metode!).
     *
     * @param control
     */
    public static void addGradientListener2Widget(final Control control, final Color c1, final Color c2) {
        try {
            if ((control == null) || control.isDisposed()) {
                return;
            }
            if ((c1 == null) || c1.isDisposed()) {
                return;
            }
            if ((c2 == null) || c2.isDisposed()) {
                return;
            }
            /**
             * initializare gradient
             */
            SWTeXtension.setGradientBackground(control, c1, c2);
            /**
             * listener pentru recalcul gradient pe ev de resize
             */
            control.addListener(SWT.Resize, new Listener() {

                @Override
                public void handleEvent(final Event event) {
                    SWTeXtension.setGradientBackground(control, c1, c2);
                }
            });
        } catch (Exception exc) {
            logger.error(exc.getMessage(), exc);
        }
    }

    /**
     * Creates a tooltip over any kind of control
     *
     * @param control widget-ul param
     * @param message mesajul care va fi afisat
     */
    public static void addToolTipListener(final Control control, final String message) {
        try {
            if ((control == null) || control.isDisposed()) {
                return;
            }
            Listener tipListener = new Listener() {

                private Shell tip = null;

                private Label label = null;

                void createToolTip() {
                    setTip(new Shell(SWT.ON_TOP));
                    getTip().setLayout(new FillLayout());
                    setLabel(new Label(this.getTip(), SWT.NONE));
                    Listener listener = new Listener() {

                        @Override
                        public void handleEvent(final Event event) {
                            getTip().dispose();
                        }
                    };

                    getLabel().addListener(SWT.MouseExit, listener);
                    getLabel().setForeground(ColorUtil.COLOR_FOCUS_YELLOW);
                    getLabel().setBackground(ColorUtil.COLOR_FOCUS_YELLOW);
                    getLabel().setText(message);
                    getTip().pack();
                }

                @Override
                public void handleEvent(final Event e) {
                    switch (e.type) {
                        case SWT.KeyDown:
                        case SWT.MouseMove:
                            if (getTip() != null) {
                                getTip().dispose();
                            }
                            setTip(null);
                            break;
                        case SWT.Resize:
                            if (getTip() != null) {
                                getTip().dispose();
                            }
                            setTip(null);
                            break;
                        case SWT.MouseExit:
                            if (getTip() != null) {
                                getTip().dispose();
                            }
                            setTip(null);
                            break;
                        case SWT.MouseHover: {
                            if (getTip() != null) {
                                break;
                            }
                            createToolTip();
                            Rectangle rect = getTip().getBounds();
                            rect.x = e.x;
                            rect.y = e.y + 22;
                            getTip().setBounds(Display.getDefault().map(control, null, rect));
                            getTip().setVisible(true);
                            break;
                        }
                        default:
                    }
                }

                public void setTip(Shell tip) {
                    this.tip = tip;
                }

                public Shell getTip() {
                    return this.tip;
                }

                public void setLabel(Label label) {
                    this.label = label;
                }

                public Label getLabel() {
                    return this.label;
                }
            };
            control.addListener(SWT.KeyDown, tipListener);
            control.addListener(SWT.MouseHover, tipListener);
            control.addListener(SWT.MouseMove, tipListener);
            control.addListener(SWT.MouseExit, tipListener);
            control.addListener(SWT.Resize, tipListener);
        } catch (Exception exc) {
            logger.error(exc.getMessage(), exc);
        }
    }

    public static void afisareText(final ToolBar bar, final boolean showText, final String[] NAMES) {
        try {
            if ((bar == null) || bar.isDisposed() || (bar.getItemCount() == 0)) {
                return;
            }
            ToolItem[] items = bar.getItems();
            int i = 0;
            for (ToolItem item : items) {
                if ((item.getStyle() & SWT.SEPARATOR) == SWT.SEPARATOR) {
                    continue;
                }
                if (showText) {
                    try {
                        item.setText(NAMES[i++]);
                    } catch (ArrayIndexOutOfBoundsException exc) {
                        logger.error(exc.getMessage(), exc);
                    }
                } else {
                    item.setText("");
                }
            }
            bar.layout();
            bar.getParent().layout();
            if (bar.getParent().getParent() != null) {
                bar.getParent().getParent().layout();
            }
        } catch (Exception exc) {
            logger.error(exc.getMessage(), exc);
        }
    }

    public static void addImageChangeListener16(final Button button, final String imageName) {
        SWTeXtension.addImageChangeListener(button, imageName, AppImages.SIZE_16);
    }

    public static void addImageChangeListener24(final Button button, final String imageName) {
        SWTeXtension.addImageChangeListener(button, imageName, AppImages.SIZE_24);
    }

    public static void addImageChangeListener32(final Button button, final String imageName) {
        SWTeXtension.addImageChangeListener(button, imageName, AppImages.SIZE_32);
    }

    public static void addImageChangeListener(final Button button, final String imageName, final int IMAGE_SIZE) {
        Listener listenToMe;
        try {
            if ((button == null) || button.isDisposed()) {
                return;
            }
            listenToMe = new Listener() {
                @Override
                public void handleEvent(final Event event) {
                    if (event.type == SWT.MouseEnter) {
                        switch (IMAGE_SIZE) {
                            case AppImages.SIZE_16: {
                                button.setImage(AppImages.getImage16Focus(imageName));
                                break;
                            }
                            case AppImages.SIZE_24: {
                                button.setImage(AppImages.getImage24Focus(imageName));
                                break;
                            }
                            case AppImages.SIZE_32: {
                                button.setImage(AppImages.getImage32Focus(imageName));
                                break;
                            }
                            default:
                        }

                    } else if (event.type == SWT.MouseExit) {
                        switch (IMAGE_SIZE) {
                            case AppImages.SIZE_16: {
                                button.setImage(AppImages.getImage16(imageName));
                                break;
                            }
                            case AppImages.SIZE_24: {
                                button.setImage(AppImages.getImage24(imageName));
                                break;
                            }
                            case AppImages.SIZE_32: {
                                button.setImage(AppImages.getImage32(imageName));
                                break;
                            }
                            default:
                        }
                    }
                }
            };
            button.addListener(SWT.MouseEnter, listenToMe);
            button.addListener(SWT.MouseExit, listenToMe);
        } catch (Exception exc) {
            logger.error(exc.getMessage(), exc);
        }
    }

    public static void drawImageOnGroup(final Group group, final String mesajContainer) {
        SWTeXtension.drawImageOnGroup(group, null, null, mesajContainer, SWT.BEGINNING);
    }

    /**
     * @param group          un grup oarecare
     * @param image          o imagine oarecare (de preferat 16x16). Se permite null. (nu va afisa imaginea)
     * @param messageColor   (culoarea in care se va randa textul pe group)
     * @param mesajContainer un text oarecare
     * @param alignment      una din valorile SWT.BEGINING sau SWT.END - afisare la stanga sau la dreapt a briz-briz-urilor :D
     */
    public static void drawImageOnGroup(final Group group, final Image image, final Color messageColor, final String mesajContainer, final int alignment) {
        try {
            if ((group == null) || group.isDisposed()) {
                return;
            }
            group.addListener(SWT.Paint, new Listener() {
                @Override
                public void handleEvent(final Event event) {
                    if ((messageColor != null) && !messageColor.isDisposed()) {
                        event.gc.setForeground(messageColor);
                    } else {
                        event.gc.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_BLACK));
                    }
                    if ((image != null) && !image.isDisposed()) {
                        if (group.getBounds().width >= 10 + image.getBounds().width) {
                            if (alignment == SWT.BEGINNING) {
                                event.gc.drawImage(image, 10, 0);
                            } else if (alignment == SWT.END) {
                                event.gc.drawImage(image, group.getBounds().width - (10 + image.getBounds().width), 0);
                            }
                        }
                        if ((group.getBounds().width >= image.getBounds().width + event.gc.getFontMetrics().getAverageCharWidth() * mesajContainer.length() + 4)
                                && StringUtils.isNotEmpty(mesajContainer)) {
                            if (alignment == SWT.BEGINNING) {
                                event.gc.drawString(mesajContainer, 12 + image.getBounds().width, 0);
                            } else if (alignment == SWT.END) {
                                event.gc.drawString(mesajContainer,
                                        group.getBounds().width - (image.getBounds().width + event.gc.getFontMetrics().getAverageCharWidth() * mesajContainer.length()) + 4,
                                        0);
                            }
                        }
                        return;
                    }
                    if ((group.getBounds().width >= 10 + event.gc.getFontMetrics().getAverageCharWidth() * mesajContainer.length()) && StringUtils.isNotEmpty(mesajContainer)) {
                        if (alignment == SWT.BEGINNING) {
                            event.gc.drawString(mesajContainer, 10, 0);
                        } else if (alignment == SWT.END) {
                            event.gc.drawString(mesajContainer, group.getBounds().width - (event.gc.getFontMetrics().getAverageCharWidth() * mesajContainer.length()), 0);
                        }
                    }
                }
            });
        } catch (Exception exc) {
            logger.error(exc.getMessage(), exc);
        }
    }

    /**
     * Metoda calculeaza, in functie de dim curenta a rezolutiei, de dimensiunea unui shell parinte si de dimensiunea unui nou shell, copil, coordonatele pe verticala si orizontala, pentru a atasa
     * shell-ul parinte shell-ului copil, fara spatiere intre ele. Probleme pot aparea si pe verticala si pe orizontala, dar sper ca toate pb sa fie tratate aici :D.
     *
     * @param parentSize ceva de genul
     *                   <p>
     *                   new Rectangle( getShellParinte().getLocation().x, getShellParinte().getLocation().y, getShellParinte().getBounds().width, getShellParinte().getBounds().height)
     * @param childSize  getShellCopil().getBounds()
     * @return un point, relativ la Display, cu coordonatele la care va fi pozitionat shell-ul copil.
     * <p>
     * <b>Atentie: Daca in shell-ul parinte se va executa la un moment dat pack() sau layout() sau orice alta rearanjare a componentelor, care ii afecteaza dimensiunea, aceste operatii tb sa
     * se efectueze inainte de apelul metodei.
     * <p>
     * De exemplu am un shell cu dim de (800x600), pe care desenez 2 label-uri si fac pack(), moment in care dim reala, pe care o vede si user-ul devine 150x100 !! Pe de alta parte, locatia
     * lui s-a calculat folosind dimensiunea initiala de 800x600, si este evident ca nu se va mai atasa parintelui.</b>
     * </p>
     */
    public static Point computeChildLocation(final Rectangle parentSize, final Rectangle childSize) {
        Point childLocation = new Point(0, 0);
        try {
            // x
            if (childSize.width + parentSize.x + parentSize.width < Display.getDefault().getPrimaryMonitor().getBounds().width) {
                childLocation.x = parentSize.width + parentSize.x;
            } else if (parentSize.x > childSize.width) {
                childLocation.x = parentSize.x - childSize.width;
            }
            // y
            if (childSize.height + parentSize.y < Display.getDefault().getPrimaryMonitor().getBounds().height) {
                childLocation.y = parentSize.y;
            } else if (parentSize.y - childSize.height + parentSize.height > 0) {
                childLocation.y = parentSize.y - childSize.height + parentSize.height;
            }
            // cazul in care dim_parent+dim_copil < latimea/inaltimea rezolutiei curente
            if ((childLocation.x == 0) || (childLocation.y == 0)) {
                childLocation.x = (Display.getDefault().getBounds().width - childSize.width) / 2;
                childLocation.y = (Display.getDefault().getBounds().height - childSize.height) / 2;
            }

        } catch (Exception exc) {
            logger.error(exc.getMessage(), exc);
        }
        return childLocation;
    }

    public static void processToolBarItems(final ToolBar bar) {
        if ((bar == null) || bar.isDisposed() || ((bar.getStyle() & SWT.RIGHT) == SWT.RIGHT)) {
            return;
        }
        final ToolItem[] items = bar.getItems();
        final String[] itemNames = new String[items.length];
        int maxChars = 5;
        for (int i = 0; i < items.length; i++) {
            itemNames[i] = items[i].getText();
            if (maxChars < itemNames[i].length()) {
                maxChars = itemNames[i].length();
            }
        }
        for (int i = 0; i < itemNames.length; i++) {
            String str = itemNames[i];
            while (str.length() < maxChars - 1) {
                str = "  ".concat(str).concat("  ");
            }
            items[i].setText(str);
        }
    }

    public static boolean getModifyTrigger(Event e) {
        boolean stateMask = e.stateMask == (EncodeLive.IS_MAC ? SWT.COMMAND : SWT.CTRL);
        boolean trigger = (e.character == 'M' || e.character == 'm');
        return stateMask && trigger;
    }

    public static boolean getDeleteTrigger(Event e) {
        if (EncodeLive.IS_MAC) {
            if (e.character == SWT.DEL) {
                return true;
            }
            return e.stateMask == SWT.COMMAND && e.character == SWT.BS;
        }
        return e.character == SWT.DEL;
    }

    public static boolean getDuplicateTrigger(Event e) {
        boolean stateMask = e.stateMask == (EncodeLive.IS_MAC ? SWT.COMMAND : SWT.CTRL);
        boolean trigger = (e.character == 'D' || e.character == 'd');
        return stateMask && trigger;
    }

    public static boolean getSaveTrigger(Event e) {
        boolean stateMask = e.stateMask == (EncodeLive.IS_MAC ? SWT.COMMAND : SWT.CTRL);
        boolean trigger = (e.character == 'S' || e.character == 's');
        return stateMask && trigger;
    }

    public static boolean getAddTrigger(Event e) {
        boolean stateMask = e.stateMask == (EncodeLive.IS_MAC ? SWT.COMMAND : SWT.CTRL);
        boolean trigger = (e.character == 'N' || e.character == 'n');
        return stateMask && trigger;
    }

    public static boolean getRefreshTrigger(Event e) {
        if (e.character == SWT.F5) {
            return true;
        }
        boolean stateMask = e.stateMask == (EncodeLive.IS_MAC ? SWT.COMMAND : SWT.CTRL);
        boolean trigger = (e.character == 'R' || e.character == 'r');
        return stateMask && trigger;
    }

    public static boolean getHelpTrigger(Event e) {
        if (e.character == SWT.F1) {
            return true;
        }
        boolean stateMask = e.stateMask == (EncodeLive.IS_MAC ? SWT.COMMAND : SWT.CTRL);
        boolean trigger = (e.character == SWT.F1);
        return stateMask && trigger;
    }

    public static boolean getExportTrigger(Event e) {
        boolean stateMask = e.stateMask == (EncodeLive.IS_MAC ? SWT.COMMAND : SWT.CTRL);
        boolean trigger = (e.character == 'E' || e.character == 'e');
        return stateMask && trigger;
    }

    public static boolean getPrintTrigger(Event e) {
        boolean stateMask = e.stateMask == (EncodeLive.IS_MAC ? SWT.COMMAND : SWT.CTRL);
        boolean trigger = (e.character == 'P' || e.character == 'p');
        return stateMask && trigger;
    }

    public static void addKeyDownListeners(Widget widget, Object implementation) {
        widget.addListener(SWT.KeyDown, new Listener() {
            @Override
            public void handleEvent(Event e) {
                if (getHelpTrigger(e)) {
                    if (implementation instanceof IHelp) {
                        ((IHelp) implementation).showHelp();
                    }
                } else if (getExportTrigger(e)) {
                    if (implementation instanceof IExport) {
                        ((IExport) implementation).exportTxt();
                    }
                } else if (getPrintTrigger(e)) {
                    if (implementation instanceof IPrint) {
                        ((IPrint) implementation).printPrinter();
                    }
                } else if (getRefreshTrigger(e)) {
                    if (implementation instanceof IRefresh) {
                        ((IRefresh) implementation).refresh();
                    }
                } else if (getAddTrigger(e)) {
                    if (implementation instanceof IAdd) {
                        ((IAdd) implementation).add();
                    }
                } else if (getModifyTrigger(e)) {
                    if (implementation instanceof IModify) {
                        ((IModify) implementation).modify();
                    }
                } else if (getDeleteTrigger(e)) {
                    if (implementation instanceof IDelete) {
                        ((IDelete) implementation).delete();
                    }
                } else if (getDuplicateTrigger(e)) {
                    if (implementation instanceof IDuplicate) {
                        ((IDuplicate) implementation).duplicate();
                    }
                }
            }
        });
    }

    public static boolean selectAllTrigger(Event e) {
        boolean stateMask = e.stateMask == (EncodeLive.IS_MAC ? SWT.COMMAND : SWT.CTRL);
        boolean trigger = (e.character == 'A' || e.character == 'a');
        return stateMask && trigger;
    }

    public static void addSelectAllListener(Table table) {
        if ((table.getStyle() & SWT.MULTI) != 0) {
            table.addListener(SWT.KeyDown, new Listener() {
                @Override
                public void handleEvent(Event e) {
                    if (selectAllTrigger(e)) {
                        table.selectAll();
                    }
                }
            });
        }
    }

    public static void removeContentProposal(Widget widget) {
        while (widget.getListeners(SWT.KeyDown).length > 0) {
            widget.removeListener(SWT.KeyDown, widget.getListeners(SWT.KeyDown)[0]);
        }
        while (widget.getListeners(SWT.Traverse).length > 0) {
            widget.removeListener(SWT.Traverse, widget.getListeners(SWT.Traverse)[0]);
        }
        while (widget.getListeners(SWT.Modify).length > 0) {
            widget.removeListener(SWT.Modify, widget.getListeners(SWT.Modify)[0]);
        }
    }
}
