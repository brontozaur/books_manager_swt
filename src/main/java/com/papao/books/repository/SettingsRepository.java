package com.papao.books.repository;

import com.papao.books.model.config.*;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface SettingsRepository extends MongoRepository<AbstractSetting, String> {

    @Query(value = "{'type':'GENERAL', 'key' : ?0, 'idUser' : ?1 }")
    GeneralSetting getGeneralSetting(String key, ObjectId idUser);

    @Query(value = "{'type':'WINDOW', 'windowKey' : ?0, 'idUser' : ?1 }")
    WindowSetting getWindowSetting(String windowKey, ObjectId idUser);

    @Query(value = "{'type':'TABLE', 'clazz' : ?0, 'tableKey' : ?1, 'idUser' : ?2}")
    TableSetting getTableSetting(String clazz, String tableKey, ObjectId idUser);

    @Query(value = "{'type':'EXPORT_PDF', 'idUser' : ?0 }")
    ExportPdfSetting getExportPdfSetting(ObjectId idUser);

    @Query(value = "{'type':'EXPORT_TXT', 'idUser' : ?0 }")
    ExportTxtSetting getExportTxtSetting(ObjectId idUser);

    @Query(value = "{'type':'EXPORT_XLS', 'idUser' : ?0 }")
    ExportXlsSetting getExportXlsSetting(ObjectId idUser);

    @Query(value = "{'type':'EXPORT_RTF', 'idUser' : ?0 }")
    ExportRtfSetting getExportRtfSetting(ObjectId idUser);

    @Query(value = "{'type':'EXPORT_HTML', 'idUser' : ?0 }")
    ExportHtmlSetting getExportHtmlSetting(ObjectId idUser);

    @Transactional
    List<AbstractSetting> removeByIdUser(ObjectId idUser);

}
