package com.papao.books.repository;

import com.papao.books.model.UserActivity;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface UserActivityRepository extends MongoRepository<UserActivity, String> {

    UserActivity getByUserIdAndBookId(ObjectId userId, ObjectId bookId);

    @Transactional
    List<UserActivity> removeByUserId(ObjectId idUser);
}
