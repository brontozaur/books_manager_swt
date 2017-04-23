
package com.papao.books;

import com.papao.books.view.EncodePlatform;
import com.papao.books.view.auth.EncodeLive;
import com.papao.books.view.auth.LoginShell;
import com.papao.books.view.view.SWTeXtension;
import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private LoginShell loginShell;

    @Autowired
    private EncodePlatform encodePlatform;

    @PostConstruct
    public void open() {
        try {
            loginShell.open(true, false);
            if (loginShell.getUserAction() == SWT.OK) {
                encodePlatform.getShell().setText(encodePlatform.getShell().getText().replace("$$$", EncodeLive.getCurrentUserName()));
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

    public void closeApp(final boolean forced) {
        encodePlatform.closeApplication(forced);
    }
}
