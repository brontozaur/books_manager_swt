package com.papao.books.controller;

import com.mongodb.*;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSInputFile;
import com.papao.books.model.DocumentData;
import com.papao.books.view.providers.tree.IntValuePair;
import com.papao.books.view.providers.tree.IntValuePairsWrapper;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import javax.activation.MimetypesFileTypeMap;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class AbstractController extends Observable {

    protected final MongoTemplate mongoTemplate;
    protected GridFS gridFS;

    @Value("${app.mongo.books.collection}")
    private String booksCollectionName;

    @Value("${app.mongo.autori.collection}")
    private String autoriCollectionName;

    @Value("${app.images.folder}")
    private String appImagesFolder;

    public AbstractController(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
        this.gridFS = new GridFS(mongoTemplate.getDb());
    }

    public String getAutoriCollectionName() {
        return autoriCollectionName;
    }

    public String getBooksCollectionName() {
        return booksCollectionName;
    }

    public String getAppImagesFolder() {
        File imageFolder = new File(appImagesFolder);
        if (!imageFolder.exists() || !imageFolder.isDirectory()) {
            imageFolder.mkdirs();
        }
        return appImagesFolder;
    }

    public List<String> getDistinctFieldAsContentProposal(String collectionName, String databaseField) {
        return mongoTemplate.getCollection(collectionName).distinct(databaseField);
    }

    public GridFSDBFile getImageData(ObjectId imageId) {
        return gridFS.findOne(imageId);
    }

    public void removeImageData(ObjectId imageId) {
        gridFS.remove(imageId);
    }

    public DocumentData saveDocument(File localFile, String urlPath) throws IOException {
        GridFSInputFile gfsFile = gridFS.createFile(localFile);
        gfsFile.setFilename(localFile.getName());
        gfsFile.setContentType(new MimetypesFileTypeMap().getContentType(localFile));
        DBObject meta = new BasicDBObject();
        meta.put("localFilePath", localFile.getAbsolutePath());
        meta.put("urlFilePath", urlPath);
        gfsFile.setMetaData(meta);
        gfsFile.save();
        GridFSDBFile gridFSDBFile = getImageData((ObjectId) gfsFile.getId());
        DocumentData documentData = new DocumentData();
        documentData.setId((ObjectId) gridFSDBFile.getId());
        documentData.setFileName(gridFSDBFile.getFilename());
        return documentData;
    }

    /*

        db.carte.aggregate([
       {
          $unwind: "$idAutori"
       },
       {
          $lookup:
             {
                from: "autor",
                localField: "idAutori",
                foreignField: "_id",
                as: "ref"
            }
       },
       { $group: { _id: "$ref", count: { $sum: 1 } } }
       ])

         */
    public IntValuePairsWrapper getDistinctValuesForReferenceCollection(String mainCollectionName,
                                                                        String mainPropertyName,
                                                                        String referenceCollectionName,
                                                                        String refPropertyName,
                                                                        String refUIProperty) {
        DBCollection collection = mongoTemplate.getCollection(mainCollectionName);
        //unwind
        DBObject unwind = new BasicDBObject("$unwind", "$" + mainPropertyName);
        //lookup
        DBObject lookupArguments = new BasicDBObject();
        lookupArguments.put("from", referenceCollectionName);
        lookupArguments.put("localField", mainPropertyName);
        lookupArguments.put("foreignField", refPropertyName);
        lookupArguments.put("as", "ref");
        DBObject lookup = new BasicDBObject("$lookup", lookupArguments);
        //group - extract complete reference object, as we need both it's id and refUIProperty
        DBObject groupFields = new BasicDBObject("_id", "$ref");
        groupFields.put("count", new BasicDBObject("$sum", 1));
        DBObject group = new BasicDBObject("$group", groupFields);

        List<DBObject> pipeline = Arrays.asList(unwind, lookup, group);
        AggregationOutput output = collection.aggregate(pipeline);

        List<IntValuePair> occurrences = new ArrayList<>();

        int emptyOrNullCount = 0;
        for (DBObject distinctValue : output.results()) {
            BasicDBList refObjectProperties = (BasicDBList) distinctValue.get("_id");
            if (refObjectProperties.isEmpty()) {
                //this is the case where the main collection refers to a non-existent ref object
                continue;
            }
            BasicDBObject referenceObject = (BasicDBObject) (refObjectProperties).get(0);
            String objectId = referenceObject.get("_id").toString();
            String itemName = (String) referenceObject.get(refUIProperty);
            int count = Integer.valueOf(distinctValue.get("count").toString());
            if (StringUtils.isNotEmpty(itemName)) {
                occurrences.add(new IntValuePair(itemName, objectId, count));
            } else {
                emptyOrNullCount += count;
            }
        }

        //additional query in the mainCollection to get objects with null or empty main property name
        Query query = new Query();
        //array field is null or empty - as a suplement to "valid" fields
        Criteria criteria = new Criteria().orOperator(Criteria.where(mainPropertyName).is(null),
                Criteria.where(mainPropertyName).is(new String[]{}));
        query.addCriteria(criteria);
        long documentsWithNullOrEmptyMainPropertyName = mongoTemplate.count(query, mainCollectionName);
        emptyOrNullCount += documentsWithNullOrEmptyMainPropertyName;

        if (emptyOrNullCount > 0) {
            occurrences.add(new IntValuePair(null, null, emptyOrNullCount));
        }
        Collections.sort(occurrences, new Comparator<IntValuePair>() {
            @Override
            public int compare(IntValuePair a, IntValuePair b) {
                return a.getValue().compareTo(b.getValue());
            }
        });
        return new IntValuePairsWrapper(emptyOrNullCount == 0 ? occurrences.size() : occurrences.size() - 1, occurrences);
    }

    public IntValuePairsWrapper getDistinctStringPropertyValues(String collectionName, String propName) {
        return getDistinctStringPropertyValues(collectionName, propName, false);
    }

    /*

    db.collectionName.aggregate([
      { $group: { _id: { propName: "$propName" }, count: { $sum: 1 } } }
    ])

    //if only the first letter is needed we need this
    db.collectionname.aggregate({
        $group: { _id: { $substr: ['$propName', 0, 1] }, count: { $sum: 1 } }
    })

     */
    public IntValuePairsWrapper getDistinctStringPropertyValues(String collectionName, String propName, boolean useFirstLetter) {
        DBCollection collection = mongoTemplate.getCollection(collectionName);

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
            String itemName = (String) distinctValue.get("_id");
            int count = Integer.valueOf(distinctValue.get("count").toString());
            if (StringUtils.isNotEmpty((String) itemName)) {
                occurrences.add(new IntValuePair(itemName, itemName, count));
            } else {
                emptyOrNullCount += count;
            }
        }
        if (emptyOrNullCount > 0) {
            occurrences.add(new IntValuePair(null, null, emptyOrNullCount));
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
        db.collectionName.aggregate(

        // Unpack the propertyName array
        { $unwind: "$propertyName" },

        // Group by the propertyName values
        { $group: {
            _id: "$propertyName",
            count: { $sum: 1 }
        }}
        )
     */
    public IntValuePairsWrapper getDistinctArrayPropertyValues(String collectionName, String propertyName) {
        DBCollection collection = mongoTemplate.getCollection(collectionName);
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
        AggregationOutput output = collection.aggregate(pipeline);

        List<IntValuePair> occurrences = new ArrayList<>();

        int emptyOrNullCount = 0;
        for (DBObject distinctValue : output.results()) {
            String itemName = (String) distinctValue.get("_id");
            int count = Integer.valueOf(distinctValue.get("count").toString());
            if (StringUtils.isNotEmpty((String) itemName)) {
                occurrences.add(new IntValuePair(itemName, itemName, count));
            } else {
                emptyOrNullCount += count;
            }
        }

        Query query = new Query();
        //array field is null or empty - as a suplement to "valid" fields
        Criteria criteria = new Criteria().orOperator(Criteria.where(propertyName).is(null),
                Criteria.where(propertyName).is(new String[]{}));
        query.addCriteria(criteria);
        long booksWithEmptyOrNullArrayProperty = mongoTemplate.count(query, collectionName);
        emptyOrNullCount += booksWithEmptyOrNullArrayProperty;

        if (emptyOrNullCount > 0) {
            occurrences.add(new IntValuePair(null, null, emptyOrNullCount));
        }
        Collections.sort(occurrences, new Comparator<IntValuePair>() {
            @Override
            public int compare(IntValuePair a, IntValuePair b) {
                return a.getValue().compareTo(b.getValue());
            }
        });
        return new IntValuePairsWrapper(emptyOrNullCount == 0 ? occurrences.size() : occurrences.size() - 1, occurrences);
    }
}
