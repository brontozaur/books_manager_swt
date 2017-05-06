package com.papao.books;

import com.papao.books.controller.ApplicationReportController;
import com.papao.books.controller.AutorController;
import com.papao.books.controller.BookController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ApplicationService {

    private static BookController bookController;
    private static ApplicationReportController applicationReportController;
    private static AutorController autorController;

    @Autowired
    public ApplicationService(AutorController autorController,
                              BookController bookController,
                              ApplicationReportController applicationReportController) {
        ApplicationService.autorController = autorController;
        ApplicationService.bookController = bookController;
        ApplicationService.applicationReportController = applicationReportController;
    }

    public static BookController getBookController() {
        return bookController;
    }

    public static ApplicationReportController getApplicationReportController() {
        return applicationReportController;
    }

    public static AutorController getAutorController() {
        return autorController;
    }
}
