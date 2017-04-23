package com.papao.books.view.auth;

import com.papao.books.controller.UserController;
import com.papao.books.model.AMongodbComparator;
import com.papao.books.model.User;
import com.papao.books.view.AppImages;
import com.papao.books.view.custom.ComboImage;
import com.papao.books.view.custom.XButton;
import com.papao.books.view.custom.XButtonData;
import com.papao.books.view.user.UserView;
import com.papao.books.view.util.ColorUtil;
import com.papao.books.view.util.Constants;
import com.papao.books.view.util.FontUtil;
import com.papao.books.view.util.WidgetCompositeUtil;
import com.papao.books.view.view.AbstractCView;
import com.papao.books.view.view.AbstractView;
import com.papao.books.view.view.SWTeXtension;
import org.apache.log4j.Logger;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@org.springframework.stereotype.Component
public class LoginShell extends AbstractCView implements Listener {

    private static Logger logger = Logger.getLogger(LoginShell.class);

    private Text textPassword;
    private ComboImage comboUsers;
    private XButton buttonLogin;
    private XButton buttonExit;
    private UserController userController;

    @Autowired
    public LoginShell(UserController userController) throws Exception {
        super(null, AbstractView.MODE_NONE);
        this.userController = userController;
        addComponents();
        populateFields();

        this.textPassword.setFocus();
    }

    private void addComponents() throws Exception {
        Label tmpLabel;

        getShell().addListener(SWT.Close, this);
        getShell().setBackground(ColorUtil.COLOR_WHITE);
        GridLayoutFactory.fillDefaults().spacing(0, 0).margins(0, 0).applyTo(getContainer());
        getShell().setImages(new Image[]{AppImages.getImage16(AppImages.IMG_BORG_MAIN), AppImages.getImage24(AppImages.IMG_BORG_MAIN), AppImages.getImage32(AppImages.IMG_BORG_MAIN)});
        GridLayoutFactory.fillDefaults().numColumns(1).margins(0, 0).applyTo(getShell());

        Composite upperComp = new Composite(getContainer(), SWT.BORDER);
        GridDataFactory.fillDefaults().grab(true, true).hint(SWT.DEFAULT, 100).applyTo(upperComp);
        GridLayoutFactory.fillDefaults().numColumns(1).equalWidth(false).applyTo(upperComp);
        upperComp.setBackground(ColorUtil.COLOR_ALBASTRU_FACEBOOK);
        upperComp.setBackgroundMode(SWT.INHERIT_DEFAULT);

        tmpLabel = new Label(upperComp, SWT.NONE);
        tmpLabel.setForeground(ColorUtil.COLOR_WHITE);
        tmpLabel.setFont(FontUtil.TAHOMA14_NORMAL);
        GridDataFactory.fillDefaults().grab(true, true).align(SWT.CENTER, SWT.CENTER).applyTo(tmpLabel);
        tmpLabel.setText("Follow the white rabbit...");

        Composite compDown = new Composite(getContainer(), SWT.NONE);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(compDown);
        GridLayoutFactory.fillDefaults().numColumns(1).margins(10, 10).applyTo(compDown);
        compDown.setBackground(ColorUtil.COLOR_WHITE);
        compDown.setBackgroundMode(SWT.INHERIT_DEFAULT);

        createLoginComposite(compDown);


        Composite compDownToolBar = new Composite(compDown, SWT.NONE);
        GridDataFactory.fillDefaults().grab(true, true).align(SWT.END, SWT.CENTER).applyTo(compDownToolBar);
        GridLayoutFactory.fillDefaults().numColumns(2).equalWidth(false).extendedMargins(0, 40, 0, 0).applyTo(compDownToolBar);

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
                close(SWT.CANCEL);
            }
        });

        WidgetCompositeUtil.addColoredFocusListener2Childrens(getContainer());

        this.textPassword.setFocus();
    }

    private void createLoginComposite(Composite parent) {
        Composite compDown1 = new Composite(parent, SWT.NONE);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(compDown1);
        GridLayoutFactory.fillDefaults().numColumns(2).equalWidth(false).extendedMargins(120, 120, 20, 0).applyTo(compDown1);

        Label tmpLabel = new Label(compDown1, SWT.NONE);
        GridDataFactory.fillDefaults().span(2, 1).hint(SWT.DEFAULT, 35).applyTo(tmpLabel);

        tmpLabel = new Label(compDown1, SWT.NONE);
        tmpLabel.setText("Utilizator");

        ComboImage.CIDescriptor desc = new ComboImage.CIDescriptor();
        desc.setAddContentProposal(true);
        desc.setClazz(User.class);
        desc.setInput(new ArrayList<User>());
        desc.setTextMethodName("getNumeComplet");
        desc.setToolItemStyle(ComboImage.ADD_ADD);

        this.comboUsers = new ComboImage(compDown1, desc);
        this.comboUsers.getItemAdd().addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                UserView view = new UserView(getShell(), new User(), userController, AbstractView.MODE_ADD);
                view.open();
                if (view.getUserAction() == SWT.CANCEL) {
                    return;
                }
                User user = view.getUser();
                try {
                    populateFields();
                    comboUsers.select(user.getNumeComplet());
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                    SWTeXtension.displayMessageE("Eroare la afisarea utilizatorilor!", e);
                }
            }
        });

        GridDataFactory.fillDefaults().grab(true, false).align(SWT.FILL, SWT.BEGINNING).applyTo(this.comboUsers);
        this.comboUsers.getCombo().setToolTipText("Selectati utilizatorul cu care va veti autentifica in sistem");

        tmpLabel = new Label(compDown1, SWT.NONE);
        tmpLabel.setText("Parola");

        this.textPassword = new Text(compDown1, SWT.PASSWORD | SWT.BORDER);
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).applyTo(this.textPassword);
        this.textPassword.setToolTipText("Introduceti parola utilizatorului selectat");
        this.textPassword.addListener(SWT.KeyDown, this);
    }

    private void populateFields() throws Exception {
        List<User> users = userController.findAll();
        Collections.sort(users, AMongodbComparator.getComparator(User.class, "getNumeComplet"));
        comboUsers.setInput(userController.findAll());
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
                            close(SWT.CANCEL);
                        }
                    }
                    break;
                }
                case SWT.Close: {
                    if (e.widget == getShell()) {
                        if (super.getUserAction() != SWT.OK) {
                            quit(e);
                        }
                    }
                    break;
                }
                default:
            }
        } catch (Exception exc) {
            logger.error(exc.getMessage(), exc);
            SWTeXtension.displayMessageE(exc.getMessage(), exc);
        }
    }

    @Override
    protected void customizeView() {
        setShellStyle(SWT.CLOSE | SWT.MIN | SWT.APPLICATION_MODAL);
        setViewOptions(SWT.NONE);
        setShellImage(AppImages.getImage16(AppImages.IMG_BORG_MAIN));
        setIsUsingCoords(false);
        setShowSaveOKMessage(false);
        setUseDocking(false);
        setShellText(Constants.ENCODE_SHELL_TEXT);
        setShellWidth(550);
        setShellHeight(450);
    }

    private void quit(Event e) {
        setExitChoice(SWT.CANCEL);
        e.doit = SWTeXtension.displayMessageQ("Sunteti sigur ca doriti sa inchideti aplicatia?", "Inchidere aplicatie") == SWT.YES;
    }

    @Override
    protected boolean validate() {
        if (this.comboUsers.getSelectedElement() == null) {
            SWTeXtension.displayMessageW("Selectati un utilizator valid!");
            this.comboUsers.getCombo().setFocus();
            return false;
        }
        User usrApp = (User) this.comboUsers.getSelectedElement();
        EncodeLive.setIdUser(usrApp.getId());
        EncodeLive.setCurrentUserName(usrApp.getNumeComplet());
        LoggerMyWay.configure(LoggerMyWay.LOG_TXT, usrApp.getNumeComplet(), true);
        logger.info("**********UTILIZATOR CURENT : " + usrApp.getNumeComplet() + " **********");
        return true;
    }

    @Override
    protected void saveData() {
        close(SWT.OK);
    }

}