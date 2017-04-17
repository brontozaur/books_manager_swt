
package com.papao.books;

import com.papao.books.view.EncodePlatform;
import com.papao.books.view.view.SWTeXtension;
import org.eclipse.swt.widgets.Display;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import javax.annotation.PostConstruct;

@SpringBootApplication
@EnableMongoRepositories
@EnableCaching
public class BooksApplication {

    private static final Logger logger = LoggerFactory.getLogger(BooksApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(BooksApplication.class, args);
    }

    @Autowired
    private EncodePlatform encodePlatform;

    @PostConstruct
    public void open() {
        try {
            encodePlatform.open(false, true);

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
