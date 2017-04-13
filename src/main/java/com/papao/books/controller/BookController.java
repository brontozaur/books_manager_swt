package com.papao.books.controller;

import com.mongodb.AggregationOutput;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSInputFile;
import com.papao.books.model.Carte;
import com.papao.books.repository.CarteRepository;
import com.papao.books.view.searcheable.BookSearchType;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.UnwindOperation;
import org.springframework.stereotype.Controller;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Observable;

@Controller
public class BookController extends Observable {

    private final CarteRepository repository;
    private final MongoTemplate mongoTemplate;
    private final GridFS gridFS;

    private Page<Carte> carti;
    private BookSearchType searchType;
    private String value;
    private boolean all;

    @Autowired
    public BookController(CarteRepository repository,
                          MongoTemplate mongoTemplate) {
        this.repository = repository;
        this.mongoTemplate = mongoTemplate;
        this.gridFS = new GridFS(mongoTemplate.getDb());
    }

    public GridFSDBFile getImageData(ObjectId imageId) {
        return gridFS.findOne(imageId);
    }

    public void removeImageData(ObjectId imageId) {
        gridFS.remove(imageId);
    }

    public Carte save(Carte carte) {
        return repository.save(carte);
    }

    public GridFSInputFile createFile(File file) throws IOException {
        return gridFS.createFile(file);
    }

    public List<String> getDistinctFieldAsContentProposal(String databaseField) {
        return mongoTemplate.getCollection("carte").distinct(databaseField);
    }

    public void requestSearch(Pageable pageable) {
        this.requestSearch(this.searchType, this.value, pageable, this.all);
    }

    public Carte findOne(String id) {
        return repository.findOne(id);
    }

    public void delete(Carte carte) {
        this.repository.delete(carte);
    }

    public AggregationOutput getDistinctEdituraValue() {
        DBCollection colllection = mongoTemplate.getCollection("carte");

        /*
            http://stackoverflow.com/questions/21452674/mongos-distinct-value-count-for-two-fields-in-java
        */

        DBObject groupFields = new BasicDBObject("_id", "$editura");
        groupFields.put("count", new BasicDBObject("$sum", 1));
        DBObject group = new BasicDBObject("$group", groupFields);
        List<DBObject> pipeline = Arrays.<DBObject>asList(group);
        return colllection.aggregate(pipeline);
    }

    /*
    http://stackoverflow.com/questions/17628786/translating-mongodb-query-to-a-mongodb-java-driver-query
        db.carte.aggregate(

        // Unpack the autori array
        { $unwind: "$autori" },

        // Group by the autori values
        { $group: {
            _id: "$autori",
            count: { $sum: 1 }
        }}
        )
     */
    public AggregationOutput getDistinctAutoriValue() {
        DBCollection colllection = mongoTemplate.getCollection("carte");

        //unwind
        DBObject unwind = new BasicDBObject("$unwind", "$autori");
        //group
        UnwindOperation uw = UnwindOperation.UnwindOperationBuilder.newBuilder().path("$autori").noArrayIndex().preserveNullAndEmptyArrays();
        DBObject groupFields = new BasicDBObject("_id", "$autori");
        groupFields.put("count", new BasicDBObject("$sum", 1));
        DBObject group = new BasicDBObject("$group", groupFields);

        List<DBObject> pipeline = Arrays.<DBObject>asList(unwind, group);
        return colllection.aggregate(pipeline);
    }

    public void requestSearch(BookSearchType searchType, String value, Pageable pageable, boolean all) {
        this.searchType = searchType;
        this.value = value;
        this.all = all;
        if (all) {
            carti = repository.findAll(pageable);
        } else if (searchType == BookSearchType.EDITURA) {
            if (StringUtils.isNotEmpty(value)) {
                carti = repository.getByEdituraContainsOrderByTitluAsc(value, pageable);
            } else {
                carti = repository.getByEdituraIsNullOrEdituraIs("", pageable);
            }
        } else if (searchType == BookSearchType.AUTOR) {
            if (StringUtils.isNotEmpty(value)) {
                carti = repository.getByAutoriContainsOrderByTitluAsc(value, pageable);
            } else {
                carti = repository.getByAutoriIsNullOrAutoriIs(new String[]{""}, pageable);
            }
        }
        setChanged();
        notifyObservers();
    }

    public Page<Carte> getSearchResult() {
        return carti;
    }
}
