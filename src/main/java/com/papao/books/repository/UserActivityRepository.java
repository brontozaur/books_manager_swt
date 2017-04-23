package com.papao.books.repository;

import com.papao.books.model.UserActivity;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserActivityRepository extends MongoRepository<UserActivity, String> {

    List<UserActivity> getByBookRatings_BookId(ObjectId bookId);

    UserActivity getByUserId(ObjectId bookId);
}
