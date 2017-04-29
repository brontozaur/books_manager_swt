package com.papao.books.controller;

import com.papao.books.model.ApplicationReport;
import com.papao.books.repository.ApplicationReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class ApplicationReportController extends AbstractController {

    private ApplicationReportRepository reportRepository;

    @Autowired
    public ApplicationReportController(MongoTemplate mongoTemplate,
                                       ApplicationReportRepository reportRepository) {
        super(mongoTemplate);
        this.reportRepository = reportRepository;
    }

    public List<ApplicationReport> getReports(boolean all, String type) {
        if (all) {
            return reportRepository.findAll();
        }
        return reportRepository.getByType(type);
    }

    public ApplicationReport save(ApplicationReport report) {
        return reportRepository.save(report);
    }
}
