package com.papao.books.ui.auth;

import com.github.haixing_hu.swt.panel.BlurredPanel;
import com.novocode.naf.swt.custom.BalloonNotification;
import com.papao.books.ApplicationService;
import com.papao.books.controller.SettingsController;
import com.papao.books.controller.UserController;
import com.papao.books.model.AMongodbComparator;
import com.papao.books.model.User;
import com.papao.books.model.config.GeneralSetting;
import com.papao.books.ui.AppImages;
import com.papao.books.ui.custom.ComboImage;
import com.papao.books.ui.custom.XButton;
import com.papao.books.ui.custom.XButtonData;
import com.papao.books.ui.user.UserView;
import com.papao.books.ui.util.ColorUtil;
import com.papao.books.ui.util.Constants;
import com.papao.books.ui.util.FontUtil;
import com.papao.books.ui.util.WidgetCompositeUtil;
import com.papao.books.ui.view.AbstractCView;
import com.papao.books.ui.view.AbstractView;
import com.papao.books.ui.view.SWTeXtension;
import org.apache.log4j.Logger;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.*;

import java.util.ArrayList;
import java.util.List;

public class LoginShell extends AbstractCView implements Listener {

    private static Logger logger = Logger.getLogger(LoginShell.class);

    private Text textPassword;
    private ComboImage comboUsers;
    private BlurredPanel bp;
    private Label easterEggLabel;

    public LoginShell() throws Exception {
        super(null, AbstractView.MODE_NONE);

        bp = new BlurredPanel(getShell());

        addComponents();
        populateFields();

        this.textPassword.setFocus();
    }

    private void addComponents() throws Exception {

        getShell().addListener(SWT.Close, this);
        getShell().setBackground(ColorUtil.COLOR_WHITE);
        GridLayoutFactory.fillDefaults().spacing(0, 0).margins(0, 0).applyTo(getContainer());
        getShell().setImages(new Image[]{AppImages.getImage16(AppImages.IMG_BOOKS_MAIN), AppImages.getImage24(AppImages.IMG_BOOKS_MAIN), AppImages.getImage32(AppImages.IMG_BOOKS_MAIN)});
        GridLayoutFactory.fillDefaults().numColumns(1).margins(0, 0).applyTo(getShell());

        Composite upperComp = new Composite(getContainer(), SWT.BORDER);
        GridDataFactory.fillDefaults().grab(true, true).hint(SWT.DEFAULT, 100).applyTo(upperComp);
        GridLayoutFactory.fillDefaults().numColumns(1).equalWidth(false).applyTo(upperComp);
        upperComp.setBackground(ColorUtil.COLOR_ALBASTRU_FACEBOOK);
        upperComp.setBackgroundMode(SWT.INHERIT_DEFAULT);

        easterEggLabel = new Label(upperComp, SWT.NONE);
        easterEggLabel.setForeground(ColorUtil.COLOR_WHITE);
        easterEggLabel.setFont(FontUtil.TAHOMA14_NORMAL);
        GridDataFactory.fillDefaults().grab(true, true).align(SWT.CENTER, SWT.CENTER).applyTo(easterEggLabel);

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

        XButton buttonLogin = new XButton(compDownToolBar, data);
        buttonLogin.registerListeners(SWT.MouseUp, new Listener() {
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

        XButton buttonExit = new XButton(compDownToolBar, data);
        buttonExit.registerListeners(SWT.MouseUp, new Listener() {
            @Override
            public void handleEvent(final Event event) {
                close(SWT.CANCEL);
            }
        });

        WidgetCompositeUtil.addColoredFocusListener2Childrens(getContainer());
    }

    @Override
    public void saveAndClose(boolean closeShell) {
        if (!validate()) {
            return;
        }

        User usrApp = (User) this.comboUsers.getSelectedElement();
        EncodeLive.setIdUser(usrApp.getId());
        EncodeLive.setCurrentUserName(usrApp.getNumeComplet());
        LoggerMyWay.configure(LoggerMyWay.LOG_TXT, usrApp.getNumeComplet(), true);
        logger.info("**********UTILIZATOR CURENT : " + usrApp.getNumeComplet() + " **********");

        GeneralSetting setting = SettingsController.getGeneralSetting("searchHighlightColor");
        if (setting != null) {
            List<Integer> rgb = (List<Integer>) setting.getValue();
            SettingsController.HIGHLIGHT_COLOR = new Color(Display.getDefault(), rgb.get(0), rgb.get(1), rgb.get(2));
        }

        //remove the listeners added by content proposal to avoid SWTException on saveAndClose() using Cmd + S
        SWTeXtension.removeContentProposal(comboUsers.getCombo());
        super.saveAndClose(true);
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
                UserView view = new UserView(getShell(), new User(), AbstractView.MODE_ADD);
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
        this.comboUsers.getCombo().addListener(SWT.KeyDown, new Listener() {
            @Override
            public void handleEvent(Event event) {
                if (SWTeXtension.getSaveTrigger(event)) {
                    saveAndClose(true);
                }
            }
        });

        GridDataFactory.fillDefaults().grab(true, false).align(SWT.FILL, SWT.BEGINNING).applyTo(this.comboUsers);
        this.comboUsers.getCombo().setToolTipText("Selectați utilizatorul cu care vă veți autentifica în sistem");

        tmpLabel = new Label(compDown1, SWT.NONE);
        tmpLabel.setText("Parolă");

        this.textPassword = new Text(compDown1, SWT.PASSWORD | SWT.BORDER);
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).applyTo(this.textPassword);
        this.textPassword.setToolTipText("Introduceți parola utilizatorului selectat");
        this.textPassword.addListener(SWT.KeyDown, this);
        this.textPassword.addListener(SWT.KeyDown, new Listener() {
            @Override
            public void handleEvent(Event event) {
                if (SWTeXtension.getSaveTrigger(event)) {
                    saveAndClose(true);
                }
            }
        });
    }

    private void populateFields() throws Exception {
        List<User> users = UserController.findAll();
        users.sort(AMongodbComparator.getComparator(User.class, "getNumeComplet"));
        comboUsers.setInput(users);
        comboUsers.selectFirstLike(ApplicationService.getApplicationConfig().getDefaultUserName());
        easterEggLabel.setText(ApplicationService.getRandomWelcomeMessage());
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
        setShellImage(AppImages.getImage16(AppImages.IMG_BOOKS_MAIN));
        //cannot use coords, since coords are user-based
        setIsUsingCoords(false);
        setShowSaveOKMessage(false);
        setUseDocking(false);
        setShellText(Constants.ENCODE_SHELL_TEXT);
        setShellWidth(550);
        setShellHeight(450);
    }

    private void quit(Event e) {
        bp.show();
        setExitChoice(SWT.CANCEL);
        e.doit = SWTeXtension.displayMessageQ("Sunteți sigur că doriți să închideți aplicația?", "Închidere aplicație") == SWT.YES;
        bp.hide();
    }

    @Override
    protected boolean validate() {
        if (this.comboUsers.getSelectedElement() == null) {
//            SWTeXtension.displayMessageW("Selectati un utilizator valid!");
            BalloonNotification.showNotification(comboUsers, "Notificare", "Selectati un utilizator valid!", 1500);
            return false;
        }
        return true;
    }

    @Override
    protected void saveData() {
        close(SWT.OK);
    }

}