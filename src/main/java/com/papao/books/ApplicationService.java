package com.papao.books;

import com.papao.books.controller.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ApplicationService {

    private static SettingsController settingsController;

    private static UserController userController;

    private static AutorController autorController;
    private static BookController bookController;
    private static ApplicationReportController applicationReportController;

    @Autowired
    public ApplicationService(SettingsController settingsController,
                              UserController userController,
                              AutorController autorController,
                              BookController bookController,
                              ApplicationReportController applicationReportController) {
        ApplicationService.settingsController = settingsController;
        ApplicationService.userController = userController;
        ApplicationService.autorController = autorController;
        ApplicationService.bookController = bookController;
        ApplicationService.applicationReportController = applicationReportController;
    }

    public static SettingsController getSettingsController() {
        return settingsController;
    }

    public static UserController getUserController() {
        return userController;
    }

    public static AutorController getAutorController() {
        return autorController;
    }

    public static BookController getBookController() {
        return bookController;
    }

    public static ApplicationReportController getApplicationReportController() {
        return applicationReportController;
    }
}
