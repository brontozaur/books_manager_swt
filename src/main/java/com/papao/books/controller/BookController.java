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
import com.papao.books.view.providers.tree.IntValuePair;
import com.papao.books.view.providers.tree.IntValuePairsWrapper;
import com.papao.books.view.searcheable.BookSearchType;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Controller;

import java.io.File;
import java.io.IOException;
import java.util.*;

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

    public void requestSearch(BookSearchType searchType, String value, Pageable pageable, boolean all) {
        this.searchType = searchType;
        this.value = value;
        this.all = all;
        if (all) {
            carti = repository.findAll(pageable);
        } else if (searchType == BookSearchType.EDITURA) {
            if (StringUtils.isNotEmpty(value)) {
                carti = repository.getByEdituraContains(value, pageable);
            } else {
                carti = repository.getByEdituraIsNullOrEdituraIs("", pageable);
            }
        } else if (searchType == BookSearchType.AUTOR) {
            if (StringUtils.isNotEmpty(value)) {
                carti = repository.getByAutoriContains(value, pageable);
            } else {
                carti = repository.getByAutoriIsNullOrAutoriIsLessThanEqual(new String[]{""}, pageable);
            }
        } else if (searchType == BookSearchType.AN_APARITIE) {
            if (StringUtils.isNotEmpty(value)) {
                carti = repository.getByAnAparitieContains(value, pageable);
            } else {
                carti = repository.getByAnAparitieIsNullOrAnAparitieIs("", pageable);
            }
        } else if (searchType == BookSearchType.LIMBA) {
            if (StringUtils.isNotEmpty(value)) {
                carti = repository.getByLimbaContains(value, pageable);
            } else {
                carti = repository.getByLimbaIsNullOrLimbaIs("", pageable);
            }
        }
        setChanged();
        notifyObservers();
    }

    public IntValuePairsWrapper getDistinctStringPropertyValues(String propName) {
        DBCollection colllection = mongoTemplate.getCollection("carte");

        /*
            http://stackoverflow.com/questions/21452674/mongos-distinct-value-count-for-two-fields-in-java
        */

        DBObject groupFields = new BasicDBObject("_id", "$" + propName);
        groupFields.put("count", new BasicDBObject("$sum", 1));
        DBObject group = new BasicDBObject("$group", groupFields);
        List<DBObject> pipeline = Arrays.asList(group);
        AggregationOutput output = colllection.aggregate(pipeline);

        int totalCount = 0;
        List<IntValuePair> occurrences = new ArrayList<>();

        int emptyOrNullCount = 0;
        for (DBObject distinctEditura : output.results()) {
            Object numeEditura = distinctEditura.get("_id");
            if (numeEditura == null) {
                emptyOrNullCount++;
                continue;
            }
            int count = Integer.valueOf(distinctEditura.get("count").toString());
            if (StringUtils.isNotEmpty((String) numeEditura)) {
                occurrences.add(new IntValuePair((String) numeEditura, count));
                totalCount += count;
            } else {
                emptyOrNullCount += count;
            }
        }
        if (emptyOrNullCount > 0) {
            totalCount += emptyOrNullCount;
            occurrences.add(new IntValuePair(null, emptyOrNullCount));
        }
        Collections.sort(occurrences, new Comparator<IntValuePair>() {
            @Override
            public int compare(IntValuePair a, IntValuePair b) {
                return a.getValue().compareTo(b.getValue());
            }
        });
        return new IntValuePairsWrapper(totalCount, occurrences);
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
    public IntValuePairsWrapper getDistinctArrayPropertyValues(String propertyName) {
        DBCollection colllection = mongoTemplate.getCollection("carte");
        final String mongoProperty = "$" + propertyName;

//  the preserveNullAndEmptyArrays does in fact preserves only the null arays!!
//        Map<String, Object> unwindMap = new HashMap<>();
//        unwindMap.put("path", mongoProperty);
//        unwindMap.put("preserveNullAndEmptyArrays", true);
//        DBObject unwind = new BasicDBObject("$unwind", unwindMap);
        DBObject unwind = new BasicDBObject("$unwind", "$autori");
        //group
        DBObject groupFields = new BasicDBObject("_id", mongoProperty);
        groupFields.put("count", new BasicDBObject("$sum", 1));
        DBObject group = new BasicDBObject("$group", groupFields);

        List<DBObject> pipeline = Arrays.asList(unwind, group);
        AggregationOutput output = colllection.aggregate(pipeline);

        int totalCount = 0;
        List<IntValuePair> occurrences = new ArrayList<>();

        int emptyOrNullCount = 0;
        for (DBObject distinctValues : output.results()) {
            Object numeAutor = distinctValues.get("_id");
            if (numeAutor == null) {
                emptyOrNullCount++;
                continue;
            }
            int count = Integer.valueOf(distinctValues.get("count").toString());
            if (StringUtils.isNotEmpty((String) numeAutor)) {
                occurrences.add(new IntValuePair((String) numeAutor, count));
                totalCount += count;
            } else {
                emptyOrNullCount += count;
            }
        }

        Query query = new Query();
        //array field is null or empty - as a suplement to "valid" fields
        Criteria criteria = new Criteria().orOperator(Criteria.where(propertyName).is(null),
                Criteria.where(propertyName).is(new String[]{}));
        query.addCriteria(criteria);
        long booksWithNoAuthors = mongoTemplate.count(query, "carte");
        emptyOrNullCount += booksWithNoAuthors;

        if (emptyOrNullCount > 0) {
            totalCount += emptyOrNullCount;
            occurrences.add(new IntValuePair(null, emptyOrNullCount));
        }
        Collections.sort(occurrences, new Comparator<IntValuePair>() {
            @Override
            public int compare(IntValuePair a, IntValuePair b) {
                return a.getValue().compareTo(b.getValue());
            }
        });
        return new IntValuePairsWrapper(totalCount, occurrences);
    }

    public Page<Carte> getSearchResult() {
        return carti;
    }
}
