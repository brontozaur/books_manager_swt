package com.papao.books.repository;

import com.papao.books.model.Autor;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AutorRepository extends MongoRepository<Autor, String> {

    List<Autor> getByNumeCompletContainsIgnoreCase(String nume);
}
