package com.papao.books.repository;

import com.papao.books.export.ExportType;
import com.papao.books.model.ApplicationReport;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportRepository extends MongoRepository<ApplicationReport, String> {

    List<ApplicationReport> getByType(ExportType type);
}
