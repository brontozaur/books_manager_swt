package com.papao.books;

import com.papao.books.config.ApplicationConfig;
import com.papao.books.controller.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.Random;

@Service
public class ApplicationService {

    private static BookController bookController;
    private static ApplicationConfig applicationConfig;
    private static Properties welcomeMessages;

    private static final Logger logger = Logger.getLogger(ApplicationService.class);

    @Autowired
    public ApplicationService(BookController bookController,
                              ApplicationConfig applicationConfig,
                              UserController userController,
                              SettingsController settingsController,
                              AutorController autorController,
                              ReportController reportController,
                              ApplicationController applicationController) {
        ApplicationService.bookController = bookController;
        ApplicationService.applicationConfig = applicationConfig;

        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("welcome.properties").getFile());
        try (FileInputStream fis = new FileInputStream(file);) {
            welcomeMessages = new Properties();
            welcomeMessages.load(fis);
        } catch (IOException iox) {
            logger.error(iox.getMessage(), iox);
        }
    }

    public static BookController getBookController() {
        return bookController;
    }

    public static ApplicationConfig getApplicationConfig() {
        return applicationConfig;
    }

    public static String getRandomWelcomeMessage() {
        int randomInt = new Random().nextInt(welcomeMessages.size());
        return welcomeMessages.getProperty(String.valueOf(randomInt));
    }
}
