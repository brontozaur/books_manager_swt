package com.papao.books;

import com.papao.books.config.ApplicationConfig;
import com.papao.books.controller.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ApplicationService {

    private static BookController bookController;
    private static ApplicationConfig applicationConfig;

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
    }

    public static BookController getBookController() {
        return bookController;
    }

    public static ApplicationConfig getApplicationConfig() {
        return applicationConfig;
    }
}
