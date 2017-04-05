package com.papao.books.auth;

import com.papao.books.BooksApplication;
import com.papao.books.model.AbstractDB;
import com.papao.books.ui.AppImages;
import com.papao.books.ui.custom.ComboImage;
import com.papao.books.ui.custom.XButton;
import com.papao.books.ui.custom.XButtonData;
import com.papao.books.ui.util.WidgetCompositeUtil;
import com.papao.books.ui.view.AbstractCView;
import com.papao.books.ui.view.AbstractView;
import com.papao.books.ui.view.SWTeXtension;
import com.papao.books.util.ColorUtil;
import com.papao.books.util.FontUtil;
import org.apache.log4j.Logger;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

public class LoginShell extends AbstractCView implements Listener {

    private static Logger logger = Logger.getLogger(LoginShell.class);

    private Text textPassword;
    private final Calendar calendar = Calendar.getInstance();
    private ComboImage comboUsers;
    private final List<AbstractDB> arrayUsers = new ArrayList<AbstractDB>();
    private XButton buttonLogin;
    private XButton buttonExit;

    public LoginShell() {
        super(null, AbstractView.MODE_NONE);
        try {
            addComponents();
            populateFields();

            this.textPassword.setFocus();
        } catch (Exception exc) {
            logger.error(exc.getMessage(), exc);
            BooksApplication.closeApp(true);
        }
    }

    private void addComponents() throws Exception {
        Label tmpLabel;

        getShell().addListener(SWT.Close, this);
        getShell().setBackground(ColorUtil.COLOR_WHITE);
        GridLayoutFactory.fillDefaults().spacing(0, 0).margins(0, 0).applyTo(getContainer());
        getShell().setImages(new Image[] { AppImages.getImage16(AppImages.IMG_BORG_MAIN), AppImages.getImage24(AppImages.IMG_BORG_MAIN), AppImages.getImage32(AppImages.IMG_BORG_MAIN) });
        GridLayoutFactory.fillDefaults().numColumns(1).margins(0, 0).applyTo(getShell());

        Composite upperComp = new Composite(getContainer(), SWT.BORDER);
        GridDataFactory.fillDefaults().grab(true, true).hint(SWT.DEFAULT, 100).applyTo(upperComp);
        GridLayoutFactory.fillDefaults().numColumns(1).equalWidth(false).applyTo(upperComp);
        upperComp.setBackground(ColorUtil.COLOR_ALBASTRU_FACEBOOK);
        upperComp.setBackgroundMode(SWT.INHERIT_DEFAULT);

        tmpLabel = new Label(upperComp, SWT.NONE);
        tmpLabel.setForeground(ColorUtil.COLOR_WHITE);
        tmpLabel.setFont(FontUtil.TAHOMA12_BOLD);
        GridDataFactory.fillDefaults().grab(true, true).align(SWT.CENTER, SWT.CENTER).applyTo(tmpLabel);

        Composite compDown = new Composite(getContainer(), SWT.NONE);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(compDown);
        GridLayoutFactory.fillDefaults().numColumns(1).margins(10, 10).applyTo(compDown);
        compDown.setBackground(ColorUtil.COLOR_WHITE);
        compDown.setBackgroundMode(SWT.INHERIT_DEFAULT);

        Composite compDown1 = new Composite(compDown, SWT.NONE);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(compDown1);
        GridLayoutFactory.fillDefaults().numColumns(2).equalWidth(false).extendedMargins(120, 120, 20, 0).applyTo(compDown1);

        tmpLabel = new Label(compDown1, SWT.NONE);
        GridDataFactory.fillDefaults().span(2, 1).hint(SWT.DEFAULT, 35).applyTo(tmpLabel);

        tmpLabel = new Label(compDown1, SWT.NONE);
        tmpLabel.setText("Utilizator");
        tmpLabel.setFont(FontUtil.TAHOMA10_BOLD);

        ComboImage.CIDescriptor desc = new ComboImage.CIDescriptor();
        desc.setAddContentProposal(true);

        this.comboUsers = new ComboImage(compDown1, desc);

        GridDataFactory.fillDefaults().grab(true, false).align(SWT.FILL, SWT.BEGINNING).applyTo(this.comboUsers);
        this.comboUsers.getCombo().setToolTipText("Selectati utilizatorul cu care va veti autentifica in sistem");
        this.comboUsers.getCombo().setFont(FontUtil.TAHOMA10_BOLD);

        tmpLabel = new Label(compDown1, SWT.NONE);
        tmpLabel.setText("Parola");
        tmpLabel.setFont(FontUtil.TAHOMA10_BOLD);

        this.textPassword = new Text(compDown1, SWT.PASSWORD | SWT.BORDER);
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).applyTo(this.textPassword);
        this.textPassword.setFont(FontUtil.TAHOMA10_BOLD);
        this.textPassword.setToolTipText("Introduceti parola utilizatorului selectat");
        this.textPassword.addListener(SWT.KeyDown, this);

        Composite compDownToolBar = new Composite(compDown, SWT.NONE);
        GridDataFactory.fillDefaults().grab(true, true).align(SWT.END, SWT.CENTER).applyTo(compDownToolBar);
        GridLayoutFactory.fillDefaults().numColumns(3).equalWidth(false).extendedMargins(0, 40, 0, 0).applyTo(compDownToolBar);

        XButtonData data = new XButtonData();
        data.setImage(AppImages.getImage32(AppImages.IMG_OK));
        data.setHotImage(AppImages.getImage32Focus(AppImages.IMG_OK));
        data.setLabelTextColor(ColorUtil.COLOR_ALBASTRU_FACEBOOK);
        data.setBorderColor(ColorUtil.COLOR_ALBASTRU_FACEBOOK);
        data.setMainText("Login");
        data.setWidth(55);

        this.buttonLogin = new XButton(compDownToolBar, data);
        this.buttonLogin.registerListeners(SWT.MouseUp, new Listener() {
            @Override
            public void handleEvent(final Event event) {
                saveAndClose(true);
            }
        });

        data = new XButtonData();
        data.setImage(AppImages.getImage32(AppImages.IMG_STOP));
        data.setHotImage(AppImages.getImage32Focus(AppImages.IMG_STOP));
        data.setLabelTextColor(ColorUtil.COLOR_ALBASTRU_FACEBOOK);
        data.setBorderColor(ColorUtil.COLOR_ALBASTRU_FACEBOOK);
        data.setMainText("Exit");
        data.setWidth(55);

        this.buttonExit = new XButton(compDownToolBar, data);
        this.buttonExit.registerListeners(SWT.MouseUp, new Listener() {
            @Override
            public void handleEvent(final Event event) {
                quit();
            }
        });

        WidgetCompositeUtil.addColoredFocusListener2Childrens(getContainer());

        this.textPassword.setFocus();
    }

    private void populateFields() throws Exception {
        final Iterator<AbstractDB> it = this.arrayUsers.iterator();
        ArrayList<AbstractDB> filteredUsers = new ArrayList<AbstractDB>();

    }

    private void performShellClose(final Event e) {
        boolean close = SWTeXtension.displayMessageQ("Sunteti sigur ca doriti sa inchideti aplicatia?", "Inchidere aplicatie") == SWT.NO;
        if (close) {
            e.doit = false;
        } else {
            BooksApplication.closeApp(false);
        }
    }

    @Override
    public void handleEvent(final Event e) {
        try {
            switch (e.type) {
            case SWT.KeyDown: {
                if (e.widget == this.textPassword) {
                    if (e.character == SWT.CR) {
                        saveAndClose(true);
                    } else if (e.character == SWT.ESC) {
                        quit();
                    }
                }
                break;
            }
            case SWT.Close: {
                if (e.widget == getShell()) {
                    if (super.getUserAction() != SWT.OK) {
                        performShellClose(e);
                    }
                }
                break;
            }
            default:
            }
        } catch (Exception exc) {
            logger.error(exc.getMessage(), exc);
        }
    }

    @Override
    protected void customizeView() {
        setShellStyle(SWT.CLOSE | SWT.MIN);
        setViewOptions(SWT.NONE);
        setShellImage(AppImages.getImage16(AppImages.IMG_BORG_MAIN));
        setIsUsingCoords(false);
        setShowSaveOKMessage(false);
        setUseDocking(false);
        setShellText("Books Manager");
        setShellWidth(550);
        setShellHeight(450);
    }

    private void quit() {
        if (SWTeXtension.displayMessageQ("Sunteti sigur ca doriti sa inchideti aplicatia?", "Inchidere aplicatie") == SWT.YES) {
            BooksApplication.closeApp(false);
        }
    }

    @Override
    protected boolean validate() {
        return true;
    }

    @Override
    protected void saveData() {
        try {
            EncodeLive.setAppYear(calendar.get(Calendar.YEAR));
            EncodeLive.setAppMonth(calendar.get(Calendar.MONTH));
            EncodeLive.setAppDay(calendar.get(Calendar.DAY_OF_MONTH));
            close(SWT.OK);
            new EncodeKernell().open();
        } catch (Exception exc) {
            logger.error(exc.getMessage(), exc);
            BooksApplication.closeApp(true);
        }
    }

}