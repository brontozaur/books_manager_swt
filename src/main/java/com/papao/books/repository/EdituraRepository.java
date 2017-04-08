package com.papao.books.repository;

import com.papao.books.model.Editura;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EdituraRepository extends MongoRepository<Editura, String> {
}
