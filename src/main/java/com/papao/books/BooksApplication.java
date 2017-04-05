package com.papao.books;

import com.papao.books.view.EncodePlatform;
import com.papao.books.view.auth.LoginShell;
import com.papao.books.view.view.SWTeXtension;
import org.eclipse.swt.widgets.Display;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import javax.annotation.PostConstruct;

@SpringBootApplication
@EnableMongoRepositories
public class BooksApplication {

    private static final Logger logger = LoggerFactory.getLogger(BooksApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(BooksApplication.class, args);
    }

    @PostConstruct
    public void open() {
        try {
            new LoginShell().open(false, true);

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

    public static void closeApp(final boolean forced) {
        try {
            if ((EncodePlatform.instance != null) && (EncodePlatform.instance.getAppTray() != null)) {
                EncodePlatform.instance.getAppTray().dispose();
            }
            if (forced) {
                logger.error("forced shutdown sequence initiated..");
                logger.info("**********APPLICATION TERMINATED WITH ERROR**********");
                Display.getDefault().dispose();
                Runtime.getRuntime().exit(-1);
            } else {
                logger.info("normal shutdown sequence initiated..");
            }
        } catch (Exception exc) {
            logger.error(exc.getMessage(), exc);
        } finally {
            Display.getDefault().readAndDispatch();// nu sunt sigur daca trebuie sau nu.
            Display.getDefault().dispose();
            Runtime.getRuntime().exit(0);
        }
    }
}
