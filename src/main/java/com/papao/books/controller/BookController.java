package com.papao.books.controller;

import com.mongodb.AggregationOutput;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSInputFile;
import com.papao.books.exception.UnsupportedSearchTypeException;
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
        } else {
            switch (searchType) {
                case EDITURA: {
                    if (StringUtils.isNotEmpty(value)) {
                        carti = repository.getByEdituraContainsIgnoreCase(value, pageable);
                    } else {
                        carti = repository.getByEdituraIsNullOrEdituraIs("", pageable);
                    }
                    break;
                }
                case AUTOR: {
                    if (StringUtils.isNotEmpty(value)) {
                        carti = repository.getByAutoriContainsIgnoreCase(value, pageable);
                    } else {
                        carti = repository.getByAutoriIsNullOrAutoriIsLessThanEqual(new String[]{""}, pageable);
                    }
                    break;
                }
                case TRADUCATOR: {
                    if (StringUtils.isNotEmpty(value)) {
                        carti = repository.getByTraducatoriContainsIgnoreCase(value, pageable);
                    } else {
                        carti = repository.getByTraducatoriIsNullOrTraducatoriIsLessThanEqual(new String[]{""}, pageable);
                    }
                    break;
                }
                case AN_APARITIE: {
                    if (StringUtils.isNotEmpty(value)) {
                        carti = repository.getByAnAparitieContainsIgnoreCase(value, pageable);
                    } else {
                        carti = repository.getByAnAparitieIsNullOrAnAparitieIs("", pageable);
                    }
                    break;
                }
                case LIMBA: {
                    if (StringUtils.isNotEmpty(value)) {
                        carti = repository.getByLimbaContainsIgnoreCase(value, pageable);
                    } else {
                        carti = repository.getByLimbaIsNullOrLimbaIs("", pageable);
                    }
                    break;
                }
                case TITLU: {
                    if (StringUtils.isNotEmpty(value)) {
                        carti = repository.getByTitluStartingWithIgnoreCase(value, pageable);
                    } else {
                        carti = repository.getByTitluIsNullOrTitluIs("", pageable);
                    }
                    break;
                }
                default:
                    throw new UnsupportedSearchTypeException("The search type " + searchType + " is not (yet) implemented!");
            }
        }
        setChanged();
        notifyObservers();
    }

    public IntValuePairsWrapper getDistinctStringPropertyValues(String propName) {
        return getDistinctStringPropertyValues(propName, false);
    }

    /*

    db.carte.aggregate([
      { $group: { _id: { titlu: "$titlu" }, count: { $sum: 1 } } }
    ])

    //if only the first letter is needed we need this
    db.carte.aggregate({
        $group: { _id: { $substr: ['$titlu', 0, 1] }, count: { $sum: 1 } }
    })

     */
    public IntValuePairsWrapper getDistinctStringPropertyValues(String propName, boolean useFirstLetter) {
        DBCollection collection = mongoTemplate.getCollection("carte");

        /*
            http://stackoverflow.com/questions/21452674/mongos-distinct-value-count-for-two-fields-in-java
        */

        DBObject project = null;
        if (useFirstLetter) {
            project = new BasicDBObject("$project", new BasicDBObject(propName, new BasicDBObject("$toUpper", new BasicDBObject("$substr", Arrays.asList("$" + propName, 0, 1)))));
        }
        DBObject groupFields = new BasicDBObject("_id", "$" + propName);
        groupFields.put("count", new BasicDBObject("$sum", 1));
        DBObject group = new BasicDBObject("$group", groupFields);
        List<DBObject> pipeline;
        if (useFirstLetter) {
            pipeline = Arrays.asList(project, group);
        } else {
            pipeline = Arrays.asList(group);
        }
        // [{ "$project" : { "titlu" : {$toUpper: { "$substr" : [ "$titlu" , 0 , 1]}}}}, { "$group" : { "_id" : "$titlu" , "count" : { "$sum" : 1}}}]
        AggregationOutput output = collection.aggregate(pipeline);

        List<IntValuePair> occurrences = new ArrayList<>();

        int emptyOrNullCount = 0;
        for (DBObject distinctValue : output.results()) {
            Object itemName = distinctValue.get("_id");
            if (itemName == null) {
                emptyOrNullCount++;
                continue;
            }
            int count = Integer.valueOf(distinctValue.get("count").toString());
            if (StringUtils.isNotEmpty((String) itemName)) {
                occurrences.add(new IntValuePair((String) itemName, count));
            } else {
                emptyOrNullCount += count;
            }
        }
        if (emptyOrNullCount > 0) {
            occurrences.add(new IntValuePair(null, emptyOrNullCount));
        }
        Collections.sort(occurrences, new Comparator<IntValuePair>() {
            @Override
            public int compare(IntValuePair a, IntValuePair b) {
                return a.getValue().compareTo(b.getValue());
            }
        });
        return new IntValuePairsWrapper(emptyOrNullCount == 0 ? occurrences.size() : occurrences.size() - 1, occurrences);
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
        DBObject unwind = new BasicDBObject("$unwind", mongoProperty);
        //group
        DBObject groupFields = new BasicDBObject("_id", mongoProperty);
        groupFields.put("count", new BasicDBObject("$sum", 1));
        DBObject group = new BasicDBObject("$group", groupFields);

        List<DBObject> pipeline = Arrays.asList(unwind, group);
        AggregationOutput output = colllection.aggregate(pipeline);

        List<IntValuePair> occurrences = new ArrayList<>();

        int emptyOrNullCount = 0;
        for (DBObject distinctValue : output.results()) {
            Object itemName = distinctValue.get("_id");
            if (itemName == null) {
                emptyOrNullCount++;
                continue;
            }
            int count = Integer.valueOf(distinctValue.get("count").toString());
            if (StringUtils.isNotEmpty((String) itemName)) {
                occurrences.add(new IntValuePair((String) itemName, count));
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
            occurrences.add(new IntValuePair(null, emptyOrNullCount));
        }
        Collections.sort(occurrences, new Comparator<IntValuePair>() {
            @Override
            public int compare(IntValuePair a, IntValuePair b) {
                return a.getValue().compareTo(b.getValue());
            }
        });
        return new IntValuePairsWrapper(emptyOrNullCount == 0 ? occurrences.size() : occurrences.size() - 1, occurrences);
    }

    public Page<Carte> getSearchResult() {
        return carti;
    }
}
