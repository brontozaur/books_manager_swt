package com.papao.books.controller;

import com.papao.books.export.ExportType;
import com.papao.books.model.ApplicationReport;
import com.papao.books.repository.ReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class ReportController {

    private static ReportRepository reportRepository;

    @Autowired
    public ReportController(ReportRepository reportRepository) {
        ReportController.reportRepository = reportRepository;
    }

    public static List<ApplicationReport> getReports(boolean all, ExportType type) {
        if (all) {
            return reportRepository.findAll();
        }
        return reportRepository.getByType(type);
    }

    public static ApplicationReport save(ApplicationReport report) {
        return reportRepository.save(report);
    }
}
