package com.papao.books.repository;

import com.papao.books.model.Autor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AutorRepository extends MongoRepository<Autor, String> {

    List<Autor> getByIdInOrderByNumeComplet(List<ObjectId> ids);

    Autor getByNumeComplet(String numeComplet);

    List<Autor> getByNumeCompletLikeIgnoreCaseOrTitluLikeIgnoreCase(String numeComplet, String titlu);
}
