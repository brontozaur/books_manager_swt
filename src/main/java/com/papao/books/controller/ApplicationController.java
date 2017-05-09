package com.papao.books.controller;

import com.mongodb.AggregationOutput;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSInputFile;
import com.papao.books.model.DocumentData;
import com.papao.books.ui.custom.ImageSelectorComposite;
import com.papao.books.ui.providers.tree.IntValuePair;
import com.papao.books.ui.providers.tree.IntValuePairsWrapper;
import com.papao.books.ui.util.FileTypeDetector;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.LookupOperation;
import org.springframework.data.mongodb.core.aggregation.UnwindOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
public class ApplicationController {

    private static MongoTemplate mongoTemplate;
    private static GridFS gridFS;

    @Autowired
    public ApplicationController(MongoTemplate mongoTemplate) {
        ApplicationController.mongoTemplate = mongoTemplate;
        ApplicationController.gridFS = new GridFS(mongoTemplate.getDb());
    }

    public static List<String> getDistinctFieldAsContentProposal(String collectionName, String databaseField) {
        return mongoTemplate.getCollection(collectionName).distinct(databaseField);
    }

    public static GridFSDBFile getDocumentData(ObjectId documentId) {
        return gridFS.findOne(documentId);
    }

    public static void removeDocument(ObjectId documentId) {
        gridFS.remove(documentId);
    }

    public static DocumentData saveDocument(ImageSelectorComposite selectorComposite) throws IOException {
        if (selectorComposite.getSelectedFile() == null) {
            return null;
        }
        return saveDocument(selectorComposite.getSelectedFile(), selectorComposite.getWebPath());
    }

    public static DocumentData saveDocument(File localFile, String urlPath) throws IOException {
        return saveDocument(localFile, urlPath, null);
    }

    public static DocumentData saveDocument(String localFile, String contentType) throws IOException {
        File file = new File(localFile);
        if (!file.exists() || !file.isFile()) {
            throw new IOException("File " + localFile + " is invalid!");
        }
        return saveDocument(file, null, contentType);
    }

    public static DocumentData saveDocument(File localFile, String urlPath, String contentType) throws IOException {
        GridFSInputFile gfsFile = gridFS.createFile(localFile);
        gfsFile.setFilename(localFile.getName());
        if (contentType != null) {
            gfsFile.setContentType(contentType);
        }
        gfsFile.setContentType(new FileTypeDetector().probeContentType(Paths.get(localFile.getAbsolutePath())));
        DBObject meta = new BasicDBObject();
        meta.put("localFilePath", localFile.getAbsolutePath());
        meta.put("urlFilePath", urlPath);
        gfsFile.setMetaData(meta);
        gfsFile.save();
        GridFSDBFile gridFSDBFile = getDocumentData((ObjectId) gfsFile.getId());
        DocumentData documentData = new DocumentData();
        documentData.setId((ObjectId) gridFSDBFile.getId());
        documentData.setFileName(gridFSDBFile.getFilename());
        documentData.setFilePath(localFile.getAbsolutePath());
        documentData.setContentType(gridFSDBFile.getContentType());
        documentData.setUploadDate(gridFSDBFile.getUploadDate());
        documentData.setLength(gridFSDBFile.getLength());
        return documentData;
    }

    /*

        db.carte.aggregate([
            {
                $unwind: {
                    "path": "$idAutori",
                    "preserveNullAndEmptyArrays": true
                }
            },
            {
                $lookup: {
                    from: "autor",
                    localField: "idAutori",
                    foreignField: "_id",
                    as: "ref"
                }
            },
            {$group: {_id: "$ref", count: {$sum: 1}}}
        ])

    */
    public static IntValuePairsWrapper getDistinctValuesForReferenceCollection(String localCollection,
                                                                               String localField,
                                                                               String referenceCollection,
                                                                               String refPropertyName,
                                                                               String... referenceField) {
        LookupOperation lookupAuthor = Aggregation.lookup(referenceCollection, localField, refPropertyName, "ref");
        UnwindOperation unwindRefs = Aggregation.unwind("ref", true);
        GroupOperation groupByAuthor = Aggregation.group("ref").count().as("count");
        Aggregation aggregation = Aggregation.newAggregation(lookupAuthor, unwindRefs, groupByAuthor);
        List<BasicDBObject> results = mongoTemplate.aggregate(aggregation, localCollection, BasicDBObject.class).getMappedResults();

        List<IntValuePair> occurrences = new ArrayList<>();

        int emptyOrNullCount = 0;
        for (DBObject distinctValue : results) {
            Object objectId = distinctValue.get("_id");
            int count = (int) distinctValue.get("count");
            if (objectId == null) {
                emptyOrNullCount += count;
                continue;
            }
            String itemName = (String) distinctValue.get(referenceField[0]);
            if (StringUtils.isNotEmpty(itemName)) {
                if (referenceField.length == 1) {
                    occurrences.add(new IntValuePair(itemName, objectId.toString(), count));
                } else {
                    StringBuilder displayName = new StringBuilder(itemName);
                    for (int i = 1; i < referenceField.length; i++) {
                        Object ref = distinctValue.get(referenceField[i]);
                        if (ref != null) {
                            if (ref instanceof String) {
                                if (StringUtils.isNotBlank((String) ref)) {
                                    displayName.append(", ").append(ref.toString());
                                }
                            } else {
                                displayName.append(", ").append(ref.toString());
                            }
                        }
                    }
                    occurrences.add(new IntValuePair(displayName.toString(), objectId.toString(), count));
                }
            } else {
                //authors with name not set
                emptyOrNullCount += count;
            }
        }

        if (emptyOrNullCount > 0) {
            occurrences.add(new IntValuePair(null, null, emptyOrNullCount));
        }
        return new IntValuePairsWrapper(emptyOrNullCount == 0 ? occurrences.size() : occurrences.size() - 1, occurrences);
    }

    public static IntValuePairsWrapper getDistinctStringPropertyValues(String collectionName, String propName) {
        return getDistinctStringPropertyValues(collectionName, propName, false);
    }

    /*

    db.collectionName.aggregate([
      { $group: { _id: { propName: "$propName" }, count: { $sum: 1 } } }
    ])

    //if only the first letter is needed we need this
    db.collectionname.aggregate({
        $group: { _id: { $substrCP: ['$propName', 0, 1] }, count: { $sum: 1 } }
    })

     */
    public static IntValuePairsWrapper getDistinctStringPropertyValues(String collectionName, String propName, boolean useFirstLetter) {
        DBCollection collection = mongoTemplate.getCollection(collectionName);

        /*
            http://stackoverflow.com/questions/21452674/mongos-distinct-value-count-for-two-fields-in-java
            $substr causes romanian characters error, use $substrCP instead. See
             http://stackoverflow.com/questions/43556024/mongodb-error-substrbytes-invalid-range-ending-index-is-in-the-middle-of-a-ut/43556249#43556249
        */

        DBObject project = null;
        if (useFirstLetter) {
            project = new BasicDBObject("$project", new BasicDBObject(propName, new BasicDBObject("$toUpper", new BasicDBObject("$substrCP", Arrays.asList("$" + propName, 0, 1)))));
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
    public static IntValuePairsWrapper getDistinctArrayPropertyValues(String collectionName, String propertyName) {
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
        return new IntValuePairsWrapper(emptyOrNullCount == 0 ? occurrences.size() : occurrences.size() - 1, occurrences);
    }
}
