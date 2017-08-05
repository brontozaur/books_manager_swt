package com.papao.books.config;

import com.mongodb.MongoClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.io.File;

@Configuration
public class ApplicationConfig {

    @Value("${spring.data.mongodb.host}")
    private String host;

    @Value("${spring.data.mongodb.database}")
    private String database;

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

    @Bean
    public MongoClient mongo() throws Exception {
        return new MongoClient(host);
    }

    @Bean
    public MongoTemplate mongoTemplate() throws Exception {
        return new MongoTemplate(mongo(), database);
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
}
