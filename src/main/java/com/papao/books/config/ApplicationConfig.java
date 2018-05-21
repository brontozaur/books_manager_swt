package com.papao.books.config;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;

import java.io.File;

@Configuration
public class ApplicationConfig extends AbstractMongoConfiguration {

    @Value("${spring.data.mongodb.database}")
    private String database;

    @Value("${spring.data.mongodb.uri}")
    private String mongoURI;

    @Value("${app.mongo.books.collection}")
    private String booksCollectionName;

    @Value("${app.mongo.autori.collection}")
    private String autoriCollectionName;

    @Value("${app.mongo.reports.collection}")
    private String reportsCollectionName;

    @Value("${app.mongo.useractivity.collection}")
    private String userActivityCollectionName;

    @Value("${app.images.folder}")
    private String appImagesFolder;

    @Value("${app.images.export.folder}")
    private String appImagesExportFolder;

    @Value("${app.out.folder}")
    private String appOutFolder;

    @Value("${app.default.username}")
    private String defaultUserName;

    @Value("${app.default.searchtype}")
    private String defaultSearchType;

    @Bean
    public MongoDbFactory mongoDbFactory() {
        return new SimpleMongoDbFactory(mongoClient(), this.database);
    }

    @Override
    public MongoClient mongoClient() {
        MongoClientURI uri = new MongoClientURI(mongoURI);
        return new MongoClient(uri);
    }

    @Bean
    public MongoTemplate mongoTemplate() throws Exception {
        MongoTemplate mongoTemplate = new MongoTemplate(mongoDbFactory(), mappingMongoConverter());
        return mongoTemplate;

    }

    public String getAutoriCollectionName() {
        return autoriCollectionName;
    }

    public String getBooksCollectionName() {
        return booksCollectionName;
    }

    public String getReportsCollectionName() {
        return reportsCollectionName;
    }

    public String getUserActivityCollectionName() {
        return userActivityCollectionName;
    }

    public String getAppImagesExportFolder() {
        File imageFolder = new File(System.getProperties().getProperty("user.dir") + appImagesExportFolder);
        if (!imageFolder.exists() || !imageFolder.isDirectory()) {
            imageFolder.mkdirs();
        }
        return imageFolder.getAbsolutePath();
    }

    public String getAppImagesFolder() {
        File imageFolder = new File(System.getProperties().getProperty("user.dir") + appImagesFolder);
        if (!imageFolder.exists() || !imageFolder.isDirectory()) {
            imageFolder.mkdirs();
        }
        return imageFolder.getAbsolutePath();
    }

    public String getAppOutFolder() {
        File outFolder = new File(System.getProperties().getProperty("user.dir") + appOutFolder);
        if (!outFolder.exists() || !outFolder.isDirectory()) {
            outFolder.mkdirs();
        }
        return outFolder.getAbsolutePath();
    }

    public String getDefaultUserName() {
        return defaultUserName;
    }

    public String getDefaultSearchType() {
        return defaultSearchType;
    }

    @Override
    protected String getDatabaseName() {
        return this.database;
    }
}
