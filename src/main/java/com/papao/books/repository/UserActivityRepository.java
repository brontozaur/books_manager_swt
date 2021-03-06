package com.papao.books.repository;

import com.papao.books.model.UserActivity;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface UserActivityRepository extends MongoRepository<UserActivity, String> {

    List<UserActivity> getByBookId(ObjectId bookId);

    UserActivity getByUserIdAndBookId(ObjectId userId, ObjectId bookId);

    @Transactional
    void removeByUserId(ObjectId idUser);

    List<UserActivity> getByRatingAndUserId(int rating, ObjectId userId);

    List<UserActivity> getByTranslationRatingAndUserId(int translationRating, ObjectId userId);

    List<UserActivity> getByUserId(ObjectId userId);

    List<UserActivity> getByUserIdAndCarteCititaCititaIs(ObjectId userId, boolean citita);

    List<UserActivity> getByUserIdAndReviewIsGreaterThan(ObjectId userId, String emptyString);

    List<UserActivity> getByCarteCitita_CititaIs(boolean citita);
}
