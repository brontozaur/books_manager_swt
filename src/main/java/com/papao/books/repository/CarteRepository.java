package com.papao.books.repository;

import com.papao.books.model.Carte;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CarteRepository extends MongoRepository<Carte, String> {

    //string values - contains
    Page<Carte> getByEdituraIsNullOrEdituraIs(String emptyString, Pageable pageable);

    Page<Carte> getByEdituraContains(String editura, Pageable pageable);

    Page<Carte> getByLimbaIsNullOrLimbaIs(String emptyString, Pageable pageable);

    Page<Carte> getByLimbaContains(String limba, Pageable pageable);

    Page<Carte> getByAnAparitieIsNullOrAnAparitieIs(String emptyString, Pageable pageable);

    Page<Carte> getByAnAparitieContains(String editura, Pageable pageable);

    // string values - begins with, for first letter tree items
    Page<Carte> getByTitluIsNullOrTitluIs(String emptyString, Pageable pageable);

    Page<Carte> getByTitluStartingWith(String titluStartsWith, Pageable pageable);

    //string arrays
    Page<Carte> getByAutoriIsNullOrAutoriIsLessThanEqual(String[] emptyString, Pageable pageable);

    Page<Carte> getByAutoriContains(String autor, Pageable pageable);
}
