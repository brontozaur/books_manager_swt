
package com.papao.books;

import com.papao.books.controller.UserController;
import com.papao.books.view.EncodePlatform;
import com.papao.books.view.auth.EncodeLive;
import com.papao.books.view.auth.LoginShell;
import com.papao.books.view.view.SWTeXtension;
import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.mihalis.opal.notify.Notifier;
import org.mihalis.opal.notify.NotifierColorsFactory;
import org.mihalis.opal.notify.NotifierSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import javax.annotation.PostConstruct;

@SpringBootApplication
@EnableMongoRepositories
@EnableCaching
@EnableMongoAuditing
public class BooksApplication {

    private static final Logger logger = Logger.getLogger(BooksApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(BooksApplication.class, args);
    }

    private static BooksApplication app;

    @Autowired
    private UserController userController;

    @Autowired
    private EncodePlatform encodePlatform;

    @Value("${app.notification.style}")
    String notificationStyle;

    @PostConstruct
    public void open() {
        try {
            encodePlatform.getShell().setVisible(false);
            app = this;
            EncodeLive.setNotificationUsingNotifier("default".equals(notificationStyle));
            initNotifierSettings();
            LoginShell loginShell = new LoginShell(userController);
            Notifier.setParent(loginShell.getShell());
            loginShell.open(true, false);
            if (loginShell.getUserAction() == SWT.OK) {
                encodePlatform.getShell().setText("Books Manager [utilizator: $$$]");
                encodePlatform.getShell().setText(encodePlatform.getShell().getText().replace("$$$", EncodeLive.getCurrentUserName()));
                Notifier.setParent(encodePlatform.getShell());
                encodePlatform.open();
            } else {
                closeApp(false);
            }

            /*
             * the guardian code lines that prevent the app from dying, in normal running mode
             */
            while (!Display.getDefault().isDisposed()) {
                if (!Display.getDefault().readAndDispatch()) {
                    Display.getDefault().sleep();
                }
            }
            logger.info("normal shutdown is in progress...");
            closeApp(false);
        } catch (Exception exc) {
            logger.error(exc.getMessage(), exc);
            SWTeXtension.showErrorBoxSWTInternal("A intervenit o eroare fatala la lansarea/inchiderea aplicatiei - " + exc.getMessage() + "."
                    + "\nAceasta se va inchide acum. Sugestie : contactati producatorul.");
            closeApp(true);
        }
    }

    public static BooksApplication getInstance() {
        return app;
    }

    private void closeApp(final boolean forced) {
        encodePlatform.closeApplication(forced);
    }

    @Value("${app.notifier.shellWidth}")
    int shellWidth;
    @Value("${app.notifier.shellHeight}")
    int shellHeight;
    @Value("${app.notifier.visibleMiliseconds}")
    int visibleMiliseconds;
    @Value("${app.notifier.fadeTimer}")
    int fadeTimer;
    @Value("${app.notifier.fadeOutStep}")
    int fadeOutStep;
    @Value("${app.notifier.theme}")
    NotifierColorsFactory.NotifierTheme theme;
    @Value("${app.notifier.showOnParent}")
    boolean showOnParent;
    @Value("${app.notifier.fontSize}")
    int fontSize;

    private void initNotifierSettings() {
        NotifierSettings settings = Notifier.getSettings();
        settings.setShellWidth(shellWidth);
        settings.setShellHeight(shellHeight);
        settings.setVisibleMiliseconds(visibleMiliseconds);
        settings.setFadeTimer(fadeTimer);
        settings.setFadeOutStep(fadeOutStep);
        settings.setTheme(theme);
        settings.setShowOnParent(showOnParent);
        settings.setFontSize(fontSize);
    }
}
