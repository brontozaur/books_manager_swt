package com.papao.books.controller;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.DistinctIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSInputFile;
import com.papao.books.config.BooleanSetting;
import com.papao.books.model.DocumentData;
import com.papao.books.model.TipCoperta;
import com.papao.books.model.User;
import com.papao.books.model.UserActivity;
import com.papao.books.model.config.GeneralSetting;
import com.papao.books.ui.AppImages;
import com.papao.books.ui.auth.EncodeLive;
import com.papao.books.ui.config.StilAfisareData;
import com.papao.books.ui.custom.ImageSelectorComposite;
import com.papao.books.ui.providers.tree.IntValuePair;
import com.papao.books.ui.providers.tree.IntValuePairsWrapper;
import com.papao.books.ui.providers.tree.NodeType;
import com.papao.books.ui.providers.tree.SimpleTextNode;
import com.papao.books.ui.searcheable.CategoriePret;
import com.papao.books.ui.util.BorgDateUtil;
import com.papao.books.ui.util.FileTypeDetector;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;

@Controller
public class ApplicationController {

    private static MongoTemplate mongoTemplate;
    private static GridFS gridFS;

    @Autowired
    public ApplicationController(MongoTemplate mongoTemplate) {
        ApplicationController.mongoTemplate = mongoTemplate;
        ApplicationController.gridFS = new GridFS(mongoTemplate.getMongoDbFactory().getLegacyDb());
    }

    public static List<String> getDistinctFieldAsContentProposal(String collectionName, String databaseField) {
        DistinctIterable<String> result = mongoTemplate.getCollection(collectionName).distinct(databaseField, String.class);
        List<String> stringResult = new ArrayList<>();
        for (String str : result) {
            stringResult.add(str);
        }
        return stringResult;
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
        List<Document> results = mongoTemplate.aggregate(aggregation, localCollection, Document.class).getMappedResults();

        List<IntValuePair> occurrences = new ArrayList<>();

        int emptyOrNullCount = 0;
        for (Document distinctValue : results) {
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

    /*
    To check for user activities that refers to books that no longer exist (by absurd), do this awesome check:

    var vals = db.carte.find({}, {id: 1}).map(function(a){return a._id;});
    db.userActivity.find({bookId: {$nin: vals}});
     */
    public static SimpleTextNode buildRatingTreeForCurrentUser(String refUserIdPropName,
                                                               ObjectId userId,
                                                               String groupProperty,
                                                               String rootNodeName) {

        Aggregation agg = Aggregation.newAggregation(
                Aggregation.match(Criteria.where(refUserIdPropName).is(userId)),
                Aggregation.group(groupProperty).count().as("totalForRating")
        );

        AggregationResults<Document> groupResults
                = mongoTemplate.aggregate(agg, UserActivity.class, Document.class);
        List<Document> result = groupResults.getMappedResults();

        SimpleTextNode baseNode;
        SimpleTextNode invisibleRoot = new SimpleTextNode(null);
        boolean showNumbers = SettingsController.getBoolean(BooleanSetting.LEFT_TREE_SHOW_NUMBERS);
        if (SettingsController.getBoolean(BooleanSetting.LEFT_TREE_SHOW_ALL)) {
            SimpleTextNode allNode = new SimpleTextNode(invisibleRoot, rootNodeName);
            allNode.setImage(AppImages.getImage16(AppImages.IMG_LISTA));
            allNode.setNodeType(NodeType.ALL);
            allNode.setQueryValue(null);
            if (showNumbers) {
                allNode.setName(allNode.getName() + allNode.getItemCountStr());
            }
            baseNode = allNode;
        } else {
            baseNode = invisibleRoot;
        }

        for (Document distinctValue : result) {
            Integer ratingNumber = (int) distinctValue.get("_id");
            int count = (int) distinctValue.get("totalForRating");
            SimpleTextNode ratingNode = new SimpleTextNode(baseNode, "");
            ratingNode.setQueryValue(ratingNumber);
            ratingNode.setCount(count);
            ratingNode.setInvisibleName(String.valueOf(ratingNumber));
            ratingNode.setImage(AppImages.getRatingStars(ratingNumber));
            //spacing is needed to avoid overlapping of the node name with the 5 star image
            ratingNode.setName("                " + ratingNode.getName() + ratingNode.getItemCountStr());
            baseNode.increment(count);
        }
        baseNode.modifyCount(showNumbers, false);

        return invisibleRoot;
    }

    public static SimpleTextNode buildMissingInfoTree(String collectionName) {

        SimpleTextNode invisibleRoot = new SimpleTextNode(null);
        boolean showNumbers = SettingsController.getBoolean(BooleanSetting.LEFT_TREE_SHOW_NUMBERS);
        int invisibleSortingProperty = 0;

        createNodeType(invisibleRoot, NodeType.FARA_IMAGINE, collectionName, "copertaFata", "", showNumbers, ++invisibleSortingProperty);

        // carti necitite pentru userul curent
        SimpleTextNode nodeNecitite = new SimpleTextNode(invisibleRoot, NodeType.NECITITA.getNodeName());
        nodeNecitite.setImage(AppImages.getImage16(AppImages.IMG_LISTA));
        nodeNecitite.setQueryValue(null);
        nodeNecitite.setNodeType(NodeType.NECITITA);
        Query query = new Query();
        Criteria criteria = new Criteria().andOperator(Criteria.where("userId").is(EncodeLive.getIdUser()),
                Criteria.where("carteCitita.citita").is(true));
        query.addCriteria(criteria);
        int cartiCitite = (int) mongoTemplate.count(query, "userActivity");

        query = new Query();
        int toateCartile = (int) mongoTemplate.count(query, collectionName);

        nodeNecitite.setCount(toateCartile - cartiCitite);
        nodeNecitite.setInvisibleName(String.valueOf(++invisibleSortingProperty));
        if (showNumbers) {
            nodeNecitite.setName(nodeNecitite.getName() + nodeNecitite.getItemCountStr());
        }

        // carti citite pentru userul curent
        SimpleTextNode nodeCitite = new SimpleTextNode(invisibleRoot, NodeType.CITITA.getNodeName());
        nodeCitite.setImage(AppImages.getImage16(AppImages.IMG_LISTA));
        nodeCitite.setQueryValue(null);
        nodeCitite.setNodeType(NodeType.CITITA);
        query = new Query();
        criteria = new Criteria().andOperator(Criteria.where("userId").is(EncodeLive.getIdUser()),
                Criteria.where("carteCitita.citita").is(true));
        query.addCriteria(criteria);
        nodeCitite.setCount((int) mongoTemplate.count(query, "userActivity"));
        nodeCitite.setInvisibleName(String.valueOf(++invisibleSortingProperty));
        if (showNumbers) {
            nodeCitite.setName(nodeCitite.getName() + nodeCitite.getItemCountStr());
        }

        // carti cu review pentru userul curent
        SimpleTextNode nodeCartiCuReview = new SimpleTextNode(invisibleRoot, NodeType.CU_REVIEW.getNodeName());
        nodeCartiCuReview.setImage(AppImages.getImage16(AppImages.IMG_LISTA));
        nodeCartiCuReview.setQueryValue(null);
        nodeCartiCuReview.setNodeType(NodeType.CU_REVIEW);
        query = new Query();
        criteria = new Criteria().andOperator(Criteria.where("review").gt(""),
                Criteria.where("userId").is(EncodeLive.getIdUser()));
        query.addCriteria(criteria);
        int cartiCuReview = (int) mongoTemplate.count(query, "userActivity");
        nodeCartiCuReview.setCount(cartiCuReview);
        nodeCartiCuReview.setInvisibleName(String.valueOf(++invisibleSortingProperty));
        if (showNumbers) {
            nodeCartiCuReview.setName(nodeCartiCuReview.getName() + nodeCartiCuReview.getItemCountStr());
        }

        // carti fara review pentru userul curent
        SimpleTextNode nodeCartiFaraReview = new SimpleTextNode(invisibleRoot, NodeType.FARA_REVIEW.getNodeName());
        nodeCartiFaraReview.setImage(AppImages.getImage16(AppImages.IMG_LISTA));
        nodeCartiFaraReview.setQueryValue(null);
        nodeCartiFaraReview.setNodeType(NodeType.FARA_REVIEW);
        nodeCartiFaraReview.setCount(toateCartile - cartiCuReview);
        nodeCartiFaraReview.setInvisibleName(String.valueOf(++invisibleSortingProperty));
        if (showNumbers) {
            nodeCartiFaraReview.setName(nodeCartiFaraReview.getName() + nodeCartiFaraReview.getItemCountStr());
        }

        createNodeType(invisibleRoot, NodeType.FARA_PRET, collectionName, "pret.pretIntreg", 0, showNumbers, ++invisibleSortingProperty);
        createNodeType(invisibleRoot, NodeType.FARA_DATA_CUMPARARII, collectionName, "pret.dataCumpararii", null, showNumbers, ++invisibleSortingProperty);
        createNodeType(invisibleRoot, NodeType.FARA_EDITURA, collectionName, "editura", "", showNumbers, ++invisibleSortingProperty);
        createNodeType(invisibleRoot, NodeType.FARA_TRADUCATOR, collectionName, "traducatori", new String[]{}, showNumbers, ++invisibleSortingProperty);
        createNodeType(invisibleRoot, NodeType.FARA_TIP_COPERTA, collectionName, "tipCoperta", TipCoperta.Nespecificat, showNumbers, ++invisibleSortingProperty);
        createNodeType(invisibleRoot, NodeType.FARA_LOCATIE, collectionName, "locatie", "", showNumbers, ++invisibleSortingProperty);
        createNodeType(invisibleRoot, NodeType.FARA_GEN_LITERAR, collectionName, "genLiterar", new String[]{}, showNumbers, ++invisibleSortingProperty);
        createNodeType(invisibleRoot, NodeType.FARA_TAGURI, collectionName, "tags", new String[]{}, showNumbers, ++invisibleSortingProperty);
        createNodeType(invisibleRoot, NodeType.FARA_ISBN, collectionName, "isbn", "", showNumbers, ++invisibleSortingProperty);
        createNodeType(invisibleRoot, NodeType.FARA_AN_APARITIE, collectionName, "anAparitie", "", showNumbers, ++invisibleSortingProperty);

        return invisibleRoot;
    }

    private static void createNodeType(SimpleTextNode parent,
                                       NodeType nodeType,
                                       String collectionName,
                                       String propertyName,
                                       Object emptyValue,
                                       boolean showNumbers,
                                       int sorter) {
        SimpleTextNode noImageNode = new SimpleTextNode(parent, nodeType.getNodeName());
        noImageNode.setImage(AppImages.getImage16(AppImages.IMG_LISTA));
        noImageNode.setQueryValue(null);
        noImageNode.setNodeType(nodeType);
        noImageNode.setCount((int) getDocumentsWithEmptyOrNullValues(collectionName, propertyName, emptyValue));
        noImageNode.setInvisibleName(String.valueOf(sorter));
        if (showNumbers) {
            noImageNode.setName(noImageNode.getName() + noImageNode.getItemCountStr());
        }
    }

    public static SimpleTextNode buildPriceTree(String collectionName, String propName) {

        Query query = new Query();
        Criteria criteria = new Criteria().orOperator(Criteria.where(propName).is(null),
                Criteria.where(propName).is(""));
        query.addCriteria(criteria);
        long documentsWithNoPropertySet = mongoTemplate.count(query, collectionName);

        SimpleTextNode invisibleRoot = new SimpleTextNode(null);
        boolean showNumbers = SettingsController.getBoolean(BooleanSetting.LEFT_TREE_SHOW_NUMBERS);

        SimpleTextNode noPriceValuesNode = new SimpleTextNode(invisibleRoot, "Fără informații");
        noPriceValuesNode.setImage(AppImages.getImage16(AppImages.IMG_LISTA));
        noPriceValuesNode.setQueryValue(null);
        noPriceValuesNode.setCount((int) documentsWithNoPropertySet);
        noPriceValuesNode.setInvisibleName("0");
        if (showNumbers) {
            noPriceValuesNode.setName(noPriceValuesNode.getName() + noPriceValuesNode.getItemCountStr());
        }

        for (CategoriePret categoriePret : CategoriePret.values()) {
            query = new Query();
            criteria = new Criteria().andOperator(Criteria.where(propName).gte(categoriePret.getMin()),
                    Criteria.where(propName).lt(categoriePret.getMax()));
            query.addCriteria(criteria);
            long count = mongoTemplate.count(query, collectionName);

            SimpleTextNode pretNode = new SimpleTextNode(invisibleRoot, categoriePret.getDescriere());
            pretNode.setImage(AppImages.getImage16(AppImages.IMG_LISTA));
            pretNode.setQueryValue(categoriePret);
            pretNode.setCount((int) count);
            pretNode.setInvisibleName(String.valueOf(categoriePret.getMin()));
            if (showNumbers) {
                pretNode.setName(pretNode.getName() + pretNode.getItemCountStr());
            }
        }

        return invisibleRoot;
    }

    public static SimpleTextNode buildUserActivityTree(String userIdProperty,
                                                       String filterProperty,
                                                       Object filterValue,
                                                       String rootNodeName) {

        Aggregation agg = null;
        if (filterProperty != null && filterValue != null) {
            agg = Aggregation.newAggregation(
                    Aggregation.match(Criteria.where(filterProperty).is(filterValue)),
                    Aggregation.group(userIdProperty).count().as("userBooks")
            );
        } else {
            agg = Aggregation.newAggregation(
                    Aggregation.group(userIdProperty).count().as("userBooks")
            );
        }

        AggregationResults<Document> groupResults
                = mongoTemplate.aggregate(agg, UserActivity.class, Document.class);
        List<Document> result = groupResults.getMappedResults();

        SimpleTextNode baseNode;
        SimpleTextNode invisibleRoot = new SimpleTextNode(null);
        boolean showNumbers = SettingsController.getBoolean(BooleanSetting.LEFT_TREE_SHOW_NUMBERS);
        if (SettingsController.getBoolean(BooleanSetting.LEFT_TREE_SHOW_ALL)) {
            SimpleTextNode allNode = new SimpleTextNode(invisibleRoot, rootNodeName);
            allNode.setImage(AppImages.getImage16(AppImages.IMG_LISTA));
            allNode.setNodeType(NodeType.ALL);
            allNode.setQueryValue(null);
            if (showNumbers) {
                allNode.setName(allNode.getName() + allNode.getItemCountStr());
            }
            baseNode = allNode;
        } else {
            baseNode = invisibleRoot;
        }

        for (Document distinctValue : result) {
            ObjectId userId = (ObjectId) distinctValue.get("_id");
            int count = (int) distinctValue.get("userBooks");

            User user = UserController.getById(userId);
            SimpleTextNode userNode = new SimpleTextNode(baseNode, user != null ? user.getNumeComplet() : "");
            userNode.setQueryValue(userId);
            userNode.setCount(count);
            userNode.setImage(AppImages.getImage16(AppImages.IMG_USER));
            userNode.modifyCount(showNumbers, true);

            baseNode.increment(count);
        }
        baseNode.modifyCount(showNumbers, false);

        return invisibleRoot;
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
    public static IntValuePairsWrapper getDistinctStringPropertyValues(String collectionName,
                                                                       String propName,
                                                                       boolean useFirstLetter,
                                                                       boolean includeEmpty) {
        MongoCollection collection = mongoTemplate.getCollection(collectionName);

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
        AggregateIterable<Document> output = collection.aggregate(pipeline);

        List<IntValuePair> occurrences = new ArrayList<>();

        int emptyOrNullCount = 0;
        for (Document distinctValue : output) {
            String itemName = (String) distinctValue.get("_id");
            int count = Integer.valueOf(distinctValue.get("count").toString());
            if (StringUtils.isNotEmpty((String) itemName)) {
                occurrences.add(new IntValuePair(itemName, itemName, count));
            } else {
                emptyOrNullCount += count;
            }
        }
        if (emptyOrNullCount > 0 && includeEmpty) {
            occurrences.add(new IntValuePair(null, null, emptyOrNullCount));
        }
        return new IntValuePairsWrapper(emptyOrNullCount == 0 ? occurrences.size() : occurrences.size() - 1, occurrences);
    }

    /**
     * Applies to items with full date. Using hours, minutes, seconds, distinct values exist and therefore a
     * minDate -> maxDate interval query makes sense. See {@link SimpleTextNode#getMinDate()} and {@link SimpleTextNode#getMaxDate()}
     *
     * @param collectionName
     * @param propName
     * @param isAutoExpand
     * @return
     */
    public static SimpleTextNode getDateTreeStructure(String collectionName, String propName, boolean isAutoExpand) {
        MongoCollection collection = mongoTemplate.getCollection(collectionName);

        Query query = new Query();
        Criteria criteria = new Criteria().orOperator(Criteria.where(propName).is(null),
                Criteria.where(propName).is(""));
        query.addCriteria(criteria);
        long documentsWithNoPropertySet = mongoTemplate.count(query, collectionName);

        boolean showNumbers = SettingsController.getBoolean(BooleanSetting.LEFT_TREE_SHOW_NUMBERS);

        SimpleTextNode invisibleRoot = new SimpleTextNode(null);
        if (documentsWithNoPropertySet > 0) {
            //empty node on root
            SimpleTextNode emptyNode = invisibleRoot.getChildren("");
            if (emptyNode == null) {
                emptyNode = new SimpleTextNode(invisibleRoot, "");
                emptyNode.setQueryValue(null);
            }
            emptyNode.setCount((int) documentsWithNoPropertySet);
            emptyNode.modifyCount(showNumbers, true);
            emptyNode.setInvisibleName("");
        }

        GeneralSetting stilAfisareDataInTree = SettingsController.getGeneralSetting("stilAfisareDataInTree");
        boolean afisareFull = stilAfisareDataInTree != null && ((Integer) stilAfisareDataInTree.getValue()) == StilAfisareData.AFISARE_LUNI_IN_LITERE_FULL;
        boolean afisareTipScurt = stilAfisareDataInTree != null && ((Integer) stilAfisareDataInTree.getValue()) == StilAfisareData.AFISARE_LUNI_IN_LITERE_SCURT;

        DistinctIterable<Date> list = collection.distinct(propName, Date.class);
        for (Date distinctValue : list) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(distinctValue);

            //an
            String an = String.valueOf(cal.get(Calendar.YEAR));
            SimpleTextNode yearNode = invisibleRoot.getChildren(an);
            if (yearNode == null) {
                yearNode = new SimpleTextNode(invisibleRoot, an);
                yearNode.setQueryValue(cal.getTime());
                yearNode.setNodeType(NodeType.YEAR);
                yearNode.setInvisibleName(String.valueOf(an));
            }
            yearNode.setImage(AppImages.getImage16(isAutoExpand ? AppImages.IMG_EXPAND : AppImages.IMG_COLLAPSE));

            //luna
            int luna = cal.get(Calendar.MONTH);
            StringBuilder numeLuna = new StringBuilder();
            if (afisareFull) {
                numeLuna.append(BorgDateUtil.LUNILE[luna]);
            } else if (afisareTipScurt) {
                numeLuna.append(BorgDateUtil.LUNILE_SCURT[luna]);
            } else {
                String nume = String.valueOf(luna + 1);
                if (luna < 10) {
                    nume = "0" + nume;
                }
                numeLuna.append(nume);
            }

            SimpleTextNode lunaNode = yearNode.getChildren(numeLuna.toString());
            if (lunaNode == null) {
                lunaNode = new SimpleTextNode(yearNode, numeLuna.toString());
                lunaNode.setQueryValue(cal.getTime());
                lunaNode.setNodeType(NodeType.MONTH);
                lunaNode.setInvisibleName(luna < 10 ? ("0" + luna) : String.valueOf(luna));
            }
            lunaNode.setImage(AppImages.getImage16(isAutoExpand ? AppImages.IMG_EXPAND : AppImages.IMG_COLLAPSE));

            String ziua = String.valueOf(cal.get(Calendar.DAY_OF_MONTH));
            if (ziua.length() == 1) {
                ziua = "0" + ziua;
            }
            SimpleTextNode ziuaNode = lunaNode.getChildren(ziua);
            if (ziuaNode == null) {
                ziuaNode = new SimpleTextNode(lunaNode, ziua);
                ziuaNode.setQueryValue(cal.getTime());
                ziuaNode.setNodeType(NodeType.DAY);
            }
            ziuaNode.setInvisibleName(ziua);
            ziuaNode.setImage(AppImages.getImage16(AppImages.IMG_ARROW_RIGHT));
            ziuaNode.increment();
            lunaNode.increment();
            yearNode.increment();
            ziuaNode.modifyCount(showNumbers, true);
        }

        return invisibleRoot;
    }

    /**
     * Applies to items with fixed value (e.g. same date for all items), no hours, minutes, seconds taken into account
     * Used by dataCumparare field, where all values for the same day are identical
     *
     * @param collectionName the name of the collection (e.g. carte)
     * @param propName       the mongo property
     * @param isAutoExpand   autoexpands the tree
     * @param includeEmpty   includes records with no information about this field
     * @return a fully constructed internal tree representation, ready to be plugged into a live tree
     */
    public static SimpleTextNode getShortDateTreeStructure(String collectionName, String propName, boolean isAutoExpand, boolean includeEmpty) {
        MongoCollection collection = mongoTemplate.getCollection(collectionName);

        boolean showNumbers = SettingsController.getBoolean(BooleanSetting.LEFT_TREE_SHOW_NUMBERS);
        SimpleTextNode invisibleRoot = new SimpleTextNode(null);

        GeneralSetting stilAfisareDataInTree = SettingsController.getGeneralSetting("stilAfisareDataInTree");
        boolean afisareFull = stilAfisareDataInTree != null && ((Integer) stilAfisareDataInTree.getValue()) == StilAfisareData.AFISARE_LUNI_IN_LITERE_FULL;
        boolean afisareTipScurt = stilAfisareDataInTree != null && ((Integer) stilAfisareDataInTree.getValue()) == StilAfisareData.AFISARE_LUNI_IN_LITERE_SCURT;

        DBObject groupFields = new BasicDBObject("_id", "$" + propName);
        groupFields.put("count", new BasicDBObject("$sum", 1));
        DBObject group = new BasicDBObject("$group", groupFields);
        List<DBObject> pipeline = Arrays.asList(group);
        AggregateIterable<Document> output = collection.aggregate(pipeline);

        for (Document distinctValue : output) {
            Date dataCumpararii = (Date) distinctValue.get("_id");
            int count = Integer.valueOf(distinctValue.get("count").toString());
            if (dataCumpararii == null) {
                if (includeEmpty) {
                    SimpleTextNode emptyNode = new SimpleTextNode(invisibleRoot, "");
                    emptyNode.setQueryValue(null);
                    emptyNode.setImage(AppImages.getImage16(AppImages.IMG_CALENDAR));
                    emptyNode.increment(count);
                    emptyNode.modifyCount(showNumbers, true);
                    emptyNode.setInvisibleName("");
                }
                continue;
            }

            Calendar cal = Calendar.getInstance();
            cal.setTime(dataCumpararii);

            //an
            String an = String.valueOf(cal.get(Calendar.YEAR));
            SimpleTextNode yearNode = invisibleRoot.getChildren(an);
            if (yearNode == null) {
                yearNode = new SimpleTextNode(invisibleRoot, an);
                yearNode.setQueryValue(cal.getTime());
                yearNode.setNodeType(NodeType.YEAR);
                yearNode.setInvisibleName(String.valueOf(an));
            }
            yearNode.setImage(AppImages.getImage16(isAutoExpand ? AppImages.IMG_EXPAND : AppImages.IMG_COLLAPSE));

            //luna
            int luna = cal.get(Calendar.MONTH);
            StringBuilder numeLuna = new StringBuilder();
            if (afisareFull) {
                numeLuna.append(BorgDateUtil.LUNILE[luna]);
            } else if (afisareTipScurt) {
                numeLuna.append(BorgDateUtil.LUNILE_SCURT[luna]);
            } else {
                String nume = String.valueOf(luna + 1);
                if (luna < 10) {
                    nume = "0" + nume;
                }
                numeLuna.append(nume);
            }

            SimpleTextNode lunaNode = yearNode.getChildren(numeLuna.toString());
            if (lunaNode == null) {
                lunaNode = new SimpleTextNode(yearNode, numeLuna.toString());
                lunaNode.setQueryValue(cal.getTime());
                lunaNode.setNodeType(NodeType.MONTH);
                lunaNode.setInvisibleName(String.valueOf(luna));
            }
            lunaNode.setImage(AppImages.getImage16(isAutoExpand ? AppImages.IMG_EXPAND : AppImages.IMG_COLLAPSE));

            String ziua = String.valueOf(cal.get(Calendar.DAY_OF_MONTH));
            if (ziua.length() == 1) {
                ziua = "0" + ziua;
            }
            SimpleTextNode ziuaNode = lunaNode.getChildren(ziua);
            if (ziuaNode == null) {
                ziuaNode = new SimpleTextNode(lunaNode, ziua);
                ziuaNode.setQueryValue(cal.getTime());
                ziuaNode.setNodeType(NodeType.DAY);
            }
            ziuaNode.setInvisibleName(ziua);
            ziuaNode.setImage(AppImages.getImage16(AppImages.IMG_ARROW_RIGHT));
            ziuaNode.increment(count);
            lunaNode.increment(count);
            yearNode.increment(count);
            ziuaNode.modifyCount(showNumbers, true);
        }

        return invisibleRoot;
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
        MongoCollection collection = mongoTemplate.getCollection(collectionName);
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
        AggregateIterable<Document> output = collection.aggregate(pipeline);

        List<IntValuePair> occurrences = new ArrayList<>();

        int emptyOrNullCount = 0;
        for (Document distinctValue : output) {
            String itemName = (String) distinctValue.get("_id");
            int count = Integer.valueOf(distinctValue.get("count").toString());
            if (StringUtils.isNotEmpty((String) itemName)) {
                occurrences.add(new IntValuePair(itemName, itemName, count));
            } else {
                emptyOrNullCount += count;
            }
        }

        long booksWithEmptyOrNullArrayProperty = getDocumentsWithEmptyOrNullValues(collectionName, propertyName, new String[]{});
        emptyOrNullCount += booksWithEmptyOrNullArrayProperty;

        if (emptyOrNullCount > 0) {
            occurrences.add(new IntValuePair(null, null, emptyOrNullCount));
        }
        return new IntValuePairsWrapper(emptyOrNullCount == 0 ? occurrences.size() : occurrences.size() - 1, occurrences);
    }

    private static long getDocumentsWithEmptyOrNullValues(String collectionName, String propertyName, Object emptyValue) {
        Query query = new Query();
        Criteria criteria = new Criteria().orOperator(Criteria.where(propertyName).exists(false), Criteria.where(propertyName).is(null),
                Criteria.where(propertyName).is(emptyValue));
        query.addCriteria(criteria);
        return mongoTemplate.count(query, collectionName);
    }

    public static ObjectId getRandomBook(String collectionName) {
        MongoCollection collection = mongoTemplate.getCollection(collectionName);
        DBObject sample = new BasicDBObject("$sample", new BasicDBObject("size", 1));
        List<DBObject> pipeline = Collections.singletonList(sample);
        AggregateIterable<Document> output = collection.aggregate(pipeline);
        return (ObjectId) output.iterator().next().get("_id");
    }
}
