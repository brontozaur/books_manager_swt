package com.papao.books.repository;

import com.papao.books.model.config.AbstractSetting;
import com.papao.books.model.config.GeneralSetting;
import com.papao.books.model.config.TableSetting;
import com.papao.books.model.config.WindowSetting;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AbstractSettingRepository extends MongoRepository<AbstractSetting, String> {

    @Query(value = "{'type':'GENERAL', 'key' : ?0, 'idUser' : ?1 }")
    GeneralSetting getGeneralSetting(String key, ObjectId idUser);

    @Query(value = "{'type':'WINDOW', 'windowKey' : ?0, 'idUser' : ?1 }")
    WindowSetting getWindowSetting(String windowKey, ObjectId idUser);

    @Query(value = "{'type':'TABLE', 'tableKey' : ?0, 'idUser' : ?1 }")
    TableSetting getTableSetting(String tableKey, ObjectId idUser);

}
