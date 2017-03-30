package com.papao.books.repository;

import com.papao.books.model.Carte;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CarteRepository extends MongoRepository<Carte, String> {
}
